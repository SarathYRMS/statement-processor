package com.rabo.statement.processor.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class FileUtil {

    public static List<File> readFilesFromDirectory(String currentInputDirectory){
        log.info("currentInputDirectory: "+currentInputDirectory);
        List<File> files = new ArrayList<>();
        try {
            Stream<Path> list = Files.list(Paths.get(currentInputDirectory));
            list.forEach(e -> {
                try {
                    files.add(new File(String.valueOf(e.toRealPath())));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }catch (IOException e){
            log.error("Exception while collecting files from the input directory", e.getMessage());
        }
        return files;
    }

    public static String createDirectory(String createDirectory){
        File file = new File(createDirectory);
        boolean verifyDirectory = file.mkdir();
        if(verifyDirectory) {
            log.info("Directory created successfully");
        }
        return file.getPath();
    }
}
