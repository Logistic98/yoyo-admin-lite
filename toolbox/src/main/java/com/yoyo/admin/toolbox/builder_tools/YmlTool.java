package com.yoyo.admin.toolbox.builder_tools;

import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;


/**
 * Yaml 配置文件转 Properties 配置文件工具类
 */
public class YmlTool {

    private static final String lineSeparator = "\n";

    /**
     * 将 yml 字符串化为 properties字符串
     *
     * @param yml
     * @return
     */
    public static String yamlStr2PropStr(String yml) {
        List<YmlNode> nodeList = getNodeList(yml);
        // 去掉多余数据，并打印
        String str = printNodeList(nodeList);
        return str;
    }

    /**
     * 将 yml 文件转化为 properties 文件
     *
     * @param ymlFileName 工程根目录下（非resources目录）的 yml 文件名称（如：abc.yml）
     * @return List<Node> 每个Nyml 文件中每行对应解析的数据
     */
    public static List<YmlNode> yamlFile2PropFile(String ymlFileName) {
        if (ymlFileName == null || !ymlFileName.endsWith(".yml")) {
            throw new RuntimeException("请输入yml文件名称！！");
        }
        File ymlFile = new File(ymlFileName);
        if (!ymlFile.exists()) {
            throw new RuntimeException("工程根目录下不存在 " + ymlFileName + "文件！！");
        }
        String fileName = ymlFileName.split(".yml", 2)[0];
        // 获取文件数据
        String yml = read(ymlFile);
        List<YmlNode> nodeList = getNodeList(yml);
        // 去掉多余数据，并打印
        String str = printNodeList(nodeList);
        // 将数据写入到 properties 文件中
        String propertiesName = fileName + ".properties";
        File file = new File(propertiesName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(str);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodeList;
    }

    /**
     * 将yml转化为porperties文件，并获取转化后的Map键值对
     *
     * @param ymlFileName 工程根目录下的 yml 文件名称
     * @return 转化后的 porperties 文件键值对Map
     */
    public static Map<String, String> yamlFile2Map(String ymlFileName) {
        Map<String, String> map = new HashMap<>();
        List<YmlNode> list = yamlFile2PropFile(ymlFileName);
        String s = printNodeList(list);
        String[] lines = s.split(lineSeparator);
        Stream.of(lines).forEach(line -> {
            String[] split = line.split("=");
            map.put(split[0], split[1]);
        });
        return map;
    }

    public static Map<String, String> yamlStr2Map(String yaml) {
        Map<String, String> map = new HashMap<>();
        List<YmlNode> list = getNodeList(yaml);
        String s = printNodeList(list);
        String[] lines = s.split(lineSeparator);
        Stream.of(lines).forEach(line -> {
            String[] split = line.split("=");
            map.put(split[0], split[1]);
        });
        return map;
    }

    private static String read(File file) {
        if (Objects.isNull(file) || !file.exists()) {
            return "";
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] b = new byte[(int) file.length()];
            fis.read(b);
            return new String(b, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String printNodeList(List<YmlNode> nodeList) {
        StringBuilder sb = new StringBuilder();
        for (YmlNode node : nodeList) {
            if (node.getLast().equals(Boolean.FALSE)) {
                continue;
            }
            if (node.getEmptyLine().equals(Boolean.TRUE)) {
                sb.append(lineSeparator);
                continue;
            }
            // 判断是否有行级注释
            if (node.getHeadRemark().length() > 0) {
                String s = "# " + node.getHeadRemark();
                sb.append(s).append(lineSeparator);
                continue;
            }
            // 判断是否有行末注释 (properties中注释不允许末尾注释，故而放在上面)
            if (node.getTailRemark().length() > 0) {
                String s = "# " + node.getTailRemark();
                sb.append(s).append(lineSeparator);
            }
            //
            String kv = node.getKey() + "=" + node.getValue();
            sb.append(kv).append(lineSeparator);
        }
        return sb.toString();
    }

    private static List<YmlNode> getNodeList(String yml) {
        String[] lines = yml.split(lineSeparator);
        List<YmlNode> nodeList = new ArrayList<>();
        Map<Integer, String> keyMap = new HashMap<>();
        Set<String> keySet = new HashSet<>();
        for (String line : lines) {
            YmlNode node = getNode(line);
            if (node.getKey() != null && node.getKey().length() > 0) {
                int level = node.getLevel();
                if (level == 0) {
                    keyMap.clear();
                    keyMap.put(0, node.getKey());
                } else {
                    int parentLevel = level - 1;
                    String parentKey = keyMap.get(parentLevel);
                    String currentKey = parentKey + "." + node.getKey();
                    keyMap.put(level, currentKey);
                    node.setKey(currentKey);
                }
            }
            keySet.add(node.getKey() + ".");
            nodeList.add(node);
        }
        // 标识是否最后一级
        for (YmlNode each : nodeList) {
            each.setLast(getNodeLast(each.getKey(), keySet));
        }
        return nodeList;
    }

    private static boolean getNodeLast(String key, Set<String> keySet) {
        if (key.isEmpty()) {
            return true;
        }
        key = key + ".";
        int count = 0;
        for (String each : keySet) {
            if (each.startsWith(key)) {
                count++;
            }
        }
        return count == 1;
    }

    private static YmlNode getNode(String line) {
        YmlNode node = new YmlNode();
        // 初始化默认数据（防止NPE）
        node.setEffective(Boolean.FALSE);
        node.setEmptyLine(Boolean.FALSE);
        node.setHeadRemark("");
        node.setKey("");
        node.setValue("");
        node.setTailRemark("");
        node.setLast(Boolean.FALSE);
        node.setLevel(0);
        // 空行，不处理
        String trimStr = line.trim();
        if (trimStr.isEmpty()) {
            node.setEmptyLine(Boolean.TRUE);
            return node;
        }
        // 行注释，不处理
        if (trimStr.startsWith("#")) {
            node.setHeadRemark(trimStr.replaceFirst("#", "").trim());
            return node;
        }
        // 处理值
        String[] strs = line.split(":", 2);
        // 拆分后长度为0的，属于异常数据，不做处理
        if (strs.length == 0) {
            return node;
        }
        // 获取键
        node.setKey(strs[0].trim());
        // 获取值
        String value;
        if (strs.length == 2) {
            value = strs[1];
        } else {
            value = "";
        }
        // 获取行末备注
        String tailRemark = "";
        if (value.contains(" #")) {
            String[] vs = value.split("#", 2);
            if (vs.length == 2) {
                value = vs[0];
                tailRemark = vs[1];
            }
        }
        node.setTailRemark(tailRemark.trim());
        node.setValue(value.trim());
        // 获取当前层级
        int level = getNodeLevel(line);
        node.setLevel(level);
        node.setEffective(Boolean.TRUE);
        return node;
    }

    private static int getNodeLevel(String line) {
        if (line.trim().isEmpty()) {
            return 0;
        }
        char[] chars = line.toCharArray();
        int count = 0;
        for (char c : chars) {
            if (c != ' ') {
                break;
            }
            count++;
        }
        return count / 2;
    }

    public static void main(String[] args) {
        // 把 application.yml 放在项目根目录（而不是resources目录），转换完会在根目录生成对应的 application.properties 文件
        yamlFile2PropFile("application.yml");
    }

}

@Data
class YmlNode {

    /**
     * 层级关系
     */
    private Integer level;
    /**
     * 键
     */
    private String key;
    /**
     * 值
     */
    private String value;
    /**
     * 是否为空行
     */
    private Boolean emptyLine;
    /**
     * 当前行是否为有效配置
     */
    private Boolean effective;
    /**
     * 头部注释（单行注释）
     */
    private String headRemark;
    /**
     * 末尾注释
     */
    private String tailRemark;
    /**
     * 是否为最后一层配置
     */
    private Boolean last;
}

