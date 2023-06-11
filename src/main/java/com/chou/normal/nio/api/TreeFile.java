package com.chou.normal.nio.api;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Chou
 * @Description TODO
 * @ClassName TreeFile
 * @Date 2023/6/6 22:13
 * @Version 1.0
 **/
public class TreeFile {
    public static void main(String[] args) throws IOException {
        //m1();

    }

    private static void m1() throws IOException {
        AtomicInteger fileCount = new AtomicInteger();
        AtomicInteger dirCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\soft\\jdk-11.0.16"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("Dir >>>>>>> " + dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("File >>>>>>> " + file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println(fileCount);
        System.out.println(dirCount);
    }
}
