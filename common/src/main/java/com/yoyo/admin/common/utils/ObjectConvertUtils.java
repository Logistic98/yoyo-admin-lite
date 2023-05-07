package com.yoyo.admin.common.utils;

import java.util.*;

/**
 * Object类型转换工具类
 */
public class ObjectConvertUtils {

    /**
     * 将Object转换成Integer
     * @param object
     * @return
     */
    public static Integer parseInteger(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return Integer.parseInt(object.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 将Object转换成Long
     * @param object
     * @return
     */
    public static Long parseLong(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return Long.parseLong(object.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 将Object转换成Long或默认值
     * @param object
     * @param defaultValue
     * @return
     */
    public static Long parseLongOrDefault(Object object, Long defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(object.toString());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    /**
     * 将Object转换成Double
     * @param object
     * @return
     */
    public static Double parseDouble(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return Double.parseDouble(object.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 将Object转换成Float
     * @param object
     * @return
     */
    public static Float parseFloat(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return Float.parseFloat(object.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 将Object转换成Date
     * @param object
     * @return
     */
    public static Date getAsDate(Object object) {
        if (object instanceof Date) {
            return (Date) object;
        } else {
            return null;
        }
    }

    /**
     * 将Object转换成String
     * @param object
     * @return
     */
    public static String parseString(Object object) {
        if (object == null) {
            return null;
        } else {
            return object.toString();
        }
    }

    /**
     * 将非空Object转换成String
     * @param object
     * @return
     */
    public static String getAsStringExceptEmpty(Object object) {
        if (object != null && !object.toString().isEmpty()) {
            return object.toString();
        } else {
            return null;
        }
    }

    /**
     * 将Object转换成List<Long>
     * @param object
     * @return
     */
    public static List<Long> parseLongList(Object object) {
        if (!(object instanceof List)) {
            return null;
        }
        List<?> tmpList = (List<?>) object;
        List<Long> longList = new ArrayList<>();
        if (tmpList.size() > 0) {
            try {
                tmpList.forEach(v -> {
                    longList.add(Long.parseLong(v.toString()));
                });
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        return longList;
    }

    /**
     * 将Object转换成List<String>
     * @param object
     * @return
     */
    public static List<String> parseStringList(Object object) {
        if (!(object instanceof List)) {
            return null;
        }
        List<?> tmpList = (List<?>) object;
        List<String> stringList = new ArrayList<>();
        if (tmpList.size() > 0) {
            try {
                tmpList.forEach(v -> {
                    stringList.add(v.toString());
                });
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        return stringList;
    }

    /**
     * 将Object转换成List<Map<String, Object>>
     * @param object
     * @return
     */
    public static List<Map<String, Object>> parseMapList(Object object) {
        if (!(object instanceof List)) {
            return null;
        }
        List<?> objectList = (List<?>) object;
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i <= objectList.size() - 1; i++) {
            Object o = objectList.get(i);
            if (!(o instanceof Map)) {
                continue;
            }
            Map<?, ?> objectMap = (Map<?, ?>) o;
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : objectMap.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue());
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 将各种类型的是否转成0或1
     * @param object
     * @return
     */
    public static Integer parseYesOrNoToInteger(Object object) {
        if (object == null) {
            return null;
        }
        if ("1".equals(object.toString()) || "是".equals(object.toString()) || "yes".equals(object.toString().toLowerCase())) {
            return 1;
        }
        if ("0".equals(object.toString()) || "否".equals(object.toString()) || "no".equals(object.toString().toLowerCase())) {
            return 0;
        }
        return null;
    }

    public static void main(String[] args) {

        // 制造测试数据
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> relationMapList = new ArrayList<>();
        Map<String, Object> fatherMap = new HashMap<>();
        fatherMap.put("name","lisi");
        fatherMap.put("sex",1);
        Map<String, Object> motherMap = new HashMap<>();
        motherMap.put("name","wanger");
        motherMap.put("sex",2);
        relationMapList.add(fatherMap);
        relationMapList.add(motherMap);
        List<Long> expenditureList = new ArrayList<>();
        expenditureList.add(1000L);
        expenditureList.add(2000L);
        List<String> nickNameList = new ArrayList<>();
        nickNameList.add("aaa");
        nickNameList.add("bbb");
        map.put("id", 10000L);
        map.put("name", "zhangsan");
        map.put("age", 25);
        map.put("boy", true);
        map.put("birthday", new Date());
        map.put("married", "是");
        map.put("phone", null);
        map.put("email", "");
        map.put("income", 5000.0f);
        map.put("assets", 100000.00d);
        map.put("relation", relationMapList);
        map.put("expenditure", expenditureList);
        map.put("nickName", nickNameList);
        System.out.println(map);

        // parseInteger
        Integer age = parseInteger(map.get("age"));
        System.out.println(age);

        // parseLong
        Long id = parseLong(map.get("id"));
        System.out.println(id);

        // parseLongOrDefault
        Long phone = parseLongOrDefault(map.get("phone"),0L);
        System.out.println(phone);

        // parseDouble
        Double assets = parseDouble(map.get("assets"));
        System.out.println(assets);

        // parseFloat
        Float income = parseFloat(map.get("income"));
        System.out.println(income);

        // getAsDate
        Date birthday = getAsDate(map.get("birthday"));
        System.out.println(birthday);

        // parseString
        String name = parseString(map.get("name"));
        System.out.println(name);

        // getAsStringExceptEmpty
        String email = getAsStringExceptEmpty(map.get("email"));
        System.out.println(email);

        // parseLongList
        List<Long> expenditure = parseLongList(map.get("expenditure"));
        System.out.println(expenditure);

        // parseStringList
        List<String> nickName = parseStringList(map.get("nickName"));
        System.out.println(nickName);

        // parseMapList
        List<Map<String, Object>> relation = parseMapList(map.get("relation"));
        System.out.println(relation);

        // parseYesOrNoToInteger
        Integer married = parseYesOrNoToInteger(map.get("married"));
        System.out.println(married);

    }

}
