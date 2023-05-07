package com.yoyo.admin.minio_common.util;

import cn.hutool.core.io.FileTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


public class FileTypeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileTypeUtils.class);

    private final static String IMAGE_TYPE = "image/";
    private final static String AUDIO_TYPE = "audio/";
    private final static String VIDEO_TYPE = "video/";
    private final static String APPLICATION_TYPE = "application/";
    private final static String TXT_TYPE = "text/";

    /**
     * 获取文件类型
     * @param multipartFile
     * @return
     */
    public static String getFileType(MultipartFile multipartFile) {
        InputStream inputStream = null;
        String type = null;
        try {
            inputStream = multipartFile.getInputStream();
            type = FileTypeUtil.getType(inputStream);
            LOGGER.info("FileTypeUtils | getFileType | type : " + type);
            if ("JPG".equalsIgnoreCase(type) || "JPEG".equalsIgnoreCase(type)
                    || "GIF".equalsIgnoreCase(type) || "PNG".equalsIgnoreCase(type)
                    || "BMP".equalsIgnoreCase(type) || "PCX".equalsIgnoreCase(type)
                    || "TGA".equalsIgnoreCase(type) || "PSD".equalsIgnoreCase(type)
                    || "TIFF".equalsIgnoreCase(type)) {

                LOGGER.info("FileTypeUtils | getFileType | IMAGE_TYPE+type : " + IMAGE_TYPE + type);
                return IMAGE_TYPE+type;
            }
            if ("mp3".equalsIgnoreCase(type) || "OGG".equalsIgnoreCase(type)
                    || "WAV".equalsIgnoreCase(type) || "REAL".equalsIgnoreCase(type)
                    || "APE".equalsIgnoreCase(type) || "MODULE".equalsIgnoreCase(type)
                    || "MIDI".equalsIgnoreCase(type) || "VQF".equalsIgnoreCase(type)
                    || "CD".equalsIgnoreCase(type)) {
                LOGGER.info("FileTypeUtils | getFileType | AUDIO_TYPE+type : " + AUDIO_TYPE + type);
                return AUDIO_TYPE+type;
            }
            if ("mp4".equalsIgnoreCase(type) || "avi".equalsIgnoreCase(type)
                    || "MPEG-1".equalsIgnoreCase(type) || "RM".equalsIgnoreCase(type)
                    || "ASF".equalsIgnoreCase(type) || "WMV".equalsIgnoreCase(type)
                    || "qlv".equalsIgnoreCase(type) || "MPEG-2".equalsIgnoreCase(type)
                    || "MPEG4".equalsIgnoreCase(type) || "mov".equalsIgnoreCase(type)
                    || "3gp".equalsIgnoreCase(type)) {
                LOGGER.info("FileTypeUtils | getFileType | VIDEO_TYPE+type : " + VIDEO_TYPE + type);
                return VIDEO_TYPE+type;
            }
            if ("doc".equalsIgnoreCase(type) || "docx".equalsIgnoreCase(type)
                    || "ppt".equalsIgnoreCase(type) || "pptx".equalsIgnoreCase(type)
                    || "xls".equalsIgnoreCase(type) || "xlsx".equalsIgnoreCase(type)
                    || "zip".equalsIgnoreCase(type)|| "jar".equalsIgnoreCase(type)) {
                LOGGER.info("FileTypeUtils | getFileType | APPLICATION_TYPE+type : " + APPLICATION_TYPE + type);
                return APPLICATION_TYPE+type;
            }
            if ("txt".equalsIgnoreCase(type)) {
                LOGGER.info("FileTypeUtils | getFileType | TXT_TYPE+type : " + TXT_TYPE + type);
                return TXT_TYPE+type;
            }
        }catch (IOException e){
            LOGGER.info("FileTypeUtils | getFileType | IOException : " + e.getMessage());
        }
        return null;
    }
}
