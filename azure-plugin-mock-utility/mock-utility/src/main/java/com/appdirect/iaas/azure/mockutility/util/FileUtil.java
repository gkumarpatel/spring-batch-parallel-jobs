package com.appdirect.iaas.azure.mockutility.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.SneakyThrows;

public class FileUtil {
    public static final String USAGE_SEPERATER = " Usages_";

    @SneakyThrows
    public static Path generateFolders(String filePath, String rootFolder, String numberOfLineItems) {
        StringBuilder stringBuilder = new StringBuilder(rootFolder);

        String dailyRatedFilesFolderPath = stringBuilder.append(filePath)
                .append(numberOfLineItems)
                .append(USAGE_SEPERATER)
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'hh.mm.ss"))).toString();
        return Files.createDirectories(Paths.get(dailyRatedFilesFolderPath));
    }
}
