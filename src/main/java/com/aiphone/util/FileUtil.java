package com.aiphone.util;

public class FileUtil {

    /**
     * 截取文件的扩展名
     * @param fileName 文件名
     * @return 文件的扩展名，如果没有扩展名则返回空字符串
     */
    public static String getFileExtension(String fileName) {
        // 处理空值或空字符串
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        // 找到最后一个点的位置
        int lastDotIndex = fileName.lastIndexOf('.');

        // 如果没有点，或者点是最后一个字符，则没有扩展名
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        // 截取并返回扩展名（不包含点）
        return fileName.substring(lastDotIndex + 1);
    }

    // 测试方法
    public static void main(String[] args) {
        // 测试各种文件名情况
        String[] testFiles = {
                "document.txt",
                "image.png",
                "archive.tar.gz",
                "file",
                "file.",
                ".hiddenfile",
                "my.document.pdf",
                null,
                ""
        };

        for (String file : testFiles) {
            System.out.println("文件名: " + file + " -> 扩展名: " + getFileExtension(file));
        }
    }
}
