package com.yoyo.admin.toolbox.count_tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 递归统计项目的代码行数
 */
public class CountCodeTool {

    static int count = 0;

    /**
     * 递归统计代码总行数
     * @param file
     * @param ext
     * @throws IOException
     */
    public static void myCodeCount(File file, String ext) throws IOException {
        // 文件是否为普通文件并以.java为后缀结尾
        BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        if (basicFileAttributes.isRegularFile() && (file.getName().endsWith(ext))) {
            int i = readData(file);
            count += i;
        }
        // 测试此抽象路径名表示的文件是否为目录
        if (basicFileAttributes.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File f : files) {
                f.listFiles(f1 -> f1.getName().endsWith(ext));
                myCodeCount(f, ext);
            }
        }
    }

    /**
     * 统计单个文件中的行数
     */
    public static int readData(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())));
        int i = 0;
        while ((br.readLine())!=null) {
            i++;
        }
        br.close();
        System.out.println(file.getName()+"的行数为"+i);
        return i;
    }

    public static void main(String[] args) throws IOException {
        String path = "/Users/yoyo/Workspace/jun_workspace/yoyo-admin";
        String ext = ".java";
        myCodeCount(new File(path), ext);
        System.out.println("总代码行数为"+count);
    }

}
