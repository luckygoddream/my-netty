package com.example.mynetty.chapter1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author jwq
 * @Date 2023/3/11 23:11
 * 多级目录拷贝
 * 将文件夹重命名，并复制所有文件
 */
public class TestFileCopy {
    public static void main(String[] args) throws IOException {
        // 复制test文件夹下所有目录与文件 并重新命名为te
        String source = "D:\\test";
        String target = "D:\\te";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                //是目录
                if (Files.isDirectory(path)) {
                    // 目录就创建
                    Files.createDirectory(Paths.get(targetName));
                } else if (Files.isRegularFile(path)) {//是普通文件
                    // 文件就拷贝
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
