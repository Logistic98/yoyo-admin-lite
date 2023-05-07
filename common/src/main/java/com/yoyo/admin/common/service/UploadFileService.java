package com.yoyo.admin.common.service;

import com.yoyo.admin.common.config.FileConfig;
import com.yoyo.admin.common.domain.UploadFile;
import com.yoyo.admin.common.domain.repository.UploadFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

/**
 * 上传文件服务相关接口
 */
@Service
public class UploadFileService {

    /**
     * 文档类型
     */
    private static final List<String> FILE_FORMAT_ACCEPT_ARRAY = Arrays.asList("doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt");

    /**
     * 音频类型
     */
    private static final List<String> AUDIO_FORMAT_ACCEPT_ARRAY = Arrays.asList("cda", "wave", "aiff", "mpeg ", "mp3", "mpeg-4", "midi", "wma", "amr", "flac", "wav");

    /**
     * 视频类型
     */
    private static final List<String> VIDEO_FORMAT_ACCEPT_ARRAY = Arrays.asList("avi", "mov", "rmvb", "rm", "flv", "mp4", "3gp", "mpeg", "mpg", "dat", "asf", "navi", "mkv", "webm", "ra", "wmv");


    private UploadFileRepository uploadFileRepository;
    private FileConfig fileConfig;

    private Map<String, String> fileTypes = new HashMap<>();

    @Autowired
    public void setUploadFileRepository(UploadFileRepository uploadFileRepository) {
        this.uploadFileRepository = uploadFileRepository;
    }

    @Autowired
    public void setFileConfig(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    @PostConstruct
    public void init() {
        fileTypes.put("IMAGE", "images");
        fileTypes.put("FILE", "files");
        fileTypes.put("AUDIO", "audios");
        fileTypes.put("VIDEO", "videos");
    }

    /**
     * 按id获取
     * @param id
     * @return
     */
    public UploadFile get(Long id) {
        return uploadFileRepository.findFirstById(id);
    }

    public List<UploadFile> uploadFiles(List<MultipartFile> files, String fileType) throws Exception {
        if (files.size() == 0) {
            throw new Exception("请选择要上传的文件");
        }
        List<UploadFile> fileList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                throw new Exception("不能上传空文件");
            }
            if ("IMAGE".equals(fileType)) {
                if (!isImage(file)) {
                    throw new Exception("不是图片文件");
                }
            } else if ("AUDIO".equals(fileType)) {
                if (!isAudio(file)) {
                    throw new Exception("不是音频文件");
                }
            } else if ("VIDEO".equals(fileType)) {
                if (!isVideo(file)) {
                    throw new Exception("不是视频文件");
                }
            } else if ("FILE".equals(fileType)) {
                if (!isFile(file)) {
                    throw new Exception("不是文档文件");
                }
            }
            SecureRandom random = new SecureRandom();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");
            String folderName = simpleDateFormat.format(new Date());
            File folder = new File(fileConfig.getUploadRootPath(), fileTypes.get(fileType) + "/" + folderName);
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new Exception("上传文件夹初始化失败");
                }
            }
            String originalFilename = file.getOriginalFilename();
            String extensionName = "";
            if (originalFilename != null) {
                extensionName = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }
            if (".amr".equals(extensionName) || ".wav".equals(extensionName)) {
                extensionName = ".mp3";
            }
            String filename = System.currentTimeMillis() + "_" + String.format("%04d", random.nextInt(10000)) + extensionName;
            File newFile = new File(folder, filename);
            String url = "uploads/" + fileTypes.get(fileType) + "/" + folderName + "/" + filename;
            file.transferTo(newFile);
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFilename(originalFilename);
            uploadFile.setFileType(fileType);
            uploadFile.setExtensionName(extensionName);
            uploadFile.setCompletePath(newFile.getAbsolutePath());
            uploadFile.setSize(newFile.length());
            uploadFile.setUrl(url);
            uploadFile.setUploadTime(new Date());
            uploadFileRepository.save(uploadFile);
            fileList.add(uploadFile);
        }
        return fileList;
    }

    public UploadFile uploadFile(MultipartFile file, String fileType) throws Exception {

        if (file.getSize() == 0) {
            throw new Exception("不能上传空文件");
        }
        if ("IMAGE".equals(fileType)) {
            if (!isImage(file)) {
                throw new Exception("不是图片文件");
            }
        } else if ("AUDIO".equals(fileType)) {
            if (!isAudio(file)) {
                throw new Exception("不是音频文件");
            }
        } else if ("VIDEO".equals(fileType)) {
            if (!isVideo(file)) {
                throw new Exception("不是视频文件");
            }
        } else if ("FILE".equals(fileType)) {
            if (!isFile(file)) {
                throw new Exception("不是文档文件");
            }
        }

        SecureRandom random = new SecureRandom();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String folderName = simpleDateFormat.format(new Date());
        File folder = new File(fileConfig.getUploadRootPath(), fileTypes.get(fileType) + "/" + folderName);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new Exception("上传文件夹初始化失败");
            }
        }
        String originalFilename = file.getOriginalFilename();
        String extensionName = "";
        if (originalFilename != null) {
            extensionName = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        if (".amr".equals(extensionName) || ".wav".equals(extensionName)) {
            extensionName = ".mp3";
        }
        String filename = System.currentTimeMillis() + "_" + String.format("%04d", random.nextInt(10000)) + extensionName;
        File newFile = new File(folder, filename);
        String url = "uploads/" + fileTypes.get(fileType) + "/" + folderName + "/" + filename;
        file.transferTo(newFile);
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFilename(originalFilename);
        uploadFile.setFileType(fileType);
        uploadFile.setExtensionName(extensionName);
        uploadFile.setCompletePath(newFile.getAbsolutePath());
        uploadFile.setSize(newFile.length());
        uploadFile.setUrl(url);
        uploadFile.setUploadTime(new Date());
        uploadFileRepository.save(uploadFile);
        return uploadFile;
    }

    private Boolean isImage(MultipartFile file) {
        try {
            Image image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (Exception ex) {
            return false;
        }
    }

    private Boolean isAudio(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            return AUDIO_FORMAT_ACCEPT_ARRAY.contains(fileType);
        } catch (Exception ex) {
            return false;
        }
    }

    private Boolean isVideo(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            return VIDEO_FORMAT_ACCEPT_ARRAY.contains(fileType);
        } catch (Exception ex) {
            return false;
        }
    }

    private Boolean isFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            return FILE_FORMAT_ACCEPT_ARRAY.contains(fileType);
        } catch (Exception ex) {
            return false;
        }
    }

    public static BufferedImage resizeImage(final BufferedImage bufferedimage, final int w, final int h) {
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img = new BufferedImage(w, h, type);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.drawImage(bufferedimage, 0, 0, w, h, 0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null);
        graphics2d.dispose();
        return img;
    }

}
