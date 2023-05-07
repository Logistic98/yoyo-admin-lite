package com.yoyo.admin.toolbox.builder_tools;

import com.github.lalyos.jfiglet.FigletFont;

import java.io.IOException;

/**
 * 将文本转为ASCII艺术字
 */
public class AsciiArtTool {

    public static void main(String[] args) throws IOException {
        String asciiArt= FigletFont.convertOneLine("yoyo-admin");
        System.out.println(asciiArt);
    }
}
