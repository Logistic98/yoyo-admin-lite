package com.yoyo.admin.common.service;

import com.yoyo.admin.common.domain.Area;
import com.yoyo.admin.common.domain.repository.AreaRepository;
import com.yoyo.admin.common.jpa.NativeQueryService;
import com.yoyo.admin.common.utils.ExcelUtils;
import com.yoyo.admin.common.utils.ObjectConvertUtils;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 行政区划相关接口
 */
@Service
public class AreaService {

    private AreaRepository areaRepository;

    @Autowired
    public void setAreaRepository(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    private NativeQueryService nativeQueryService;

    @Autowired
    public void setNativeQueryService(NativeQueryService nativeQueryService) {
        this.nativeQueryService = nativeQueryService;
    }

    /**
     * 获取地区数量统计
     * @return
     */
    public Long count() {
        return areaRepository.count();
    }

    /**
     * 按code获取地区
     * @param code
     * @return
     */
    public Area get(String code) {
        return areaRepository.findFirstByCode(code);
    }

    /**
     * 获取地区列表
     * @param maxGrade
     * @return
     */
    public List<Area> list(Integer maxGrade) {
        return areaRepository.findAllByGradeLessThanEqual(maxGrade);
    }

    /**
     * 查询所有地区编号
     * @param maxGrade
     * @param codes
     * @return
     */
    public List<Area> listByCodes(Integer maxGrade, List<String> codes) {
        return areaRepository.findAllByGradeLessThanEqualAndCodeIn(maxGrade, codes);
    }

    /**
     * 获取地区树形列表
     * @param maxGrade
     * @return
     */
    public List<AreaWithSubs> tree(Integer maxGrade) {
        List<Area> areas = list(maxGrade);
        return convertToTree(areas);
    }

    /**
     * 获取树形结构的地区列表
     * @param areas
     * @return
     */
    public List<AreaWithSubs> convertToTree(Collection<Area> areas) {
        List<AreaWithSubs> list = new ArrayList<>();
        if (areas == null || areas.size() == 0) {
            return list;
        }
        Integer minGrade = null;
        for (Area area : areas) {
            if (minGrade == null || area.getGrade() < minGrade) {
                minGrade = area.getGrade();
            }
        }
        for (Area area : areas) {
            if (area.getGrade().equals(minGrade)) {
                list.add(getTreeSub(area, areas));
            }
        }
        return list;
    }

    /**
     * 获取一棵完整子树
     * @param currentArea
     * @param areas
     * @return
     */
    private AreaWithSubs getTreeSub(Area currentArea, Collection<Area> areas) {
        AreaWithSubs areaWithSubs = new AreaWithSubs();
        areaWithSubs.setCode(currentArea.getCode());
        areaWithSubs.setName(currentArea.getName());
        areaWithSubs.setGrade(currentArea.getGrade());
        List<AreaWithSubs> sub = new ArrayList<>();
        for (Area area : areas) {
            if (area.getParent() != null && area.getParent().getCode().equals(currentArea.getCode())) {
                sub.add(getTreeSub(area, areas));
            }
        }
        areaWithSubs.setSub(sub);
        return areaWithSubs;
    }

    /**
     * 获取指定地区的全称
     * @param area
     * @return
     */
    public String getFullName(Area area) {
        if (area == null) {
            return "";
        }
        if (area.getParent() != null) {
            return getFullName(area.getParent()) + area.getName();
        } else {
            return area.getName();
        }
    }

    /**
     * 提取地区列表为地区代码列表
     * @param areas
     * @return
     */
    public Set<String> getAreasCodeList(List<Area> areas) {
        Set<String> codes = new HashSet<>();
        for (Area area : areas) {
            codes.add(area.getCode());
        }
        return codes;
    }

    /**
     * 提取地区列表为地区名称列表
     * @param areas
     * @return
     */
    public Set<String> getAreasNameList(List<Area> areas) {
        Set<String> names = new HashSet<>();
        for (Area area : areas) {
            names.add(getFullName(area));
        }
        return names;
    }

    /**
     * 获取指定地区列表的所有下级地区的列表
     * @param areas
     * @param maxGrade
     * @return
     */
    public List<Area> getAreasWithChildren(List<Area> areas, Integer maxGrade) {
        Set<String> areaCodes = getAreasCodeList(areas);
        List<Area> areaWithChildren = areaRepository.findAllByGradeLessThanEqualAndParent_CodeIn(maxGrade, areaCodes);
        if (areaWithChildren.size() > 0) {
            areaWithChildren.addAll(getAreasWithChildren(areaWithChildren, maxGrade));
        }
        return areaWithChildren;
    }

    @Data
    public static class AreaWithSubs {
        private String code;
        private String name;
        private Integer grade;
        private List<AreaWithSubs> sub;
    }

    /**
     * 从Excel文件中导入行政区划数据
     * @throws Exception
     */
    public void importAreasFromExcelFile() throws Exception {
        if (count() > 0) {
            return;
        }
        ClassPathResource classPathResource = new ClassPathResource("china_area.xlsx");
        if (!classPathResource.exists()) {
            return;
        }
        Sheet sheet = ExcelUtils.getFirstSheet(classPathResource.getInputStream());
        if (sheet == null) {
            throw new Exception("导入的Excel文件格式错误");
        }
        Map<String, Object> header = new HashMap<>();
        header.put("code", "code");
        header.put("name", "name");
        header.put("grade", "grade");
        header.put("parent_code", "parent_code");
        //获取并验证表头
        Map<String, Integer> headerMap = ExcelUtils.getHeaders(sheet);
        Optional<String> errorMessage = ExcelUtils.validateExcelHeader(headerMap, header);
        if (errorMessage.isPresent()) {
            throw new Exception(errorMessage.get());
        }
        //获取所有行
        List<Map<String, Object>> rows = ExcelUtils.getRows(sheet, headerMap);
        String sqlForTop = "INSERT INTO area (code,name,grade) VALUES ('%s','%s',%d)";
        String sql = "INSERT INTO area (code,name,grade,parent_code) VALUES ('%s','%s',%d,'%s')";

        List<String> sqlList = new ArrayList<>();
        //挨个处理内容行
        for (int i = 0; i <= rows.size() - 1; i++) {
            Map<String, Object> row = rows.get(i);
            String code = ObjectConvertUtils.getAsStringExceptEmpty(row.get("code")); //地区代码
            String name = ObjectConvertUtils.getAsStringExceptEmpty(row.get("name")); //地区名称
            Integer grade = ObjectConvertUtils.parseInteger(row.get("grade")); //层级
            String parentCode = ObjectConvertUtils.getAsStringExceptEmpty(row.get("parent_code")); //父级代码
            if (code == null || name == null || grade == null) {
                throw new Exception("地区文件缺少必要参数,code[" + code + "],name[" + name + "],grade[" + grade + "]");
            }
            if (parentCode == null) {
                sqlList.add(String.format(sqlForTop, code, name, grade));
            } else {
                sqlList.add(String.format(sql, code, name, grade, parentCode));
            }
        }
        nativeQueryService.batchExecute(sqlList);
    }
}
