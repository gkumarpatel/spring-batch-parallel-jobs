package com.appdirect.iaas.azure.mockutility.util;

import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.INVOICE_ID_TOKEN;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.SIZE_TOKEN;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.USAGE_TYPE_TOKEN;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.text.StringSubstitutor;

@Slf4j
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

    public static void writeResponseToJsonFile(String jsonFilePathName, String jsonResponse) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter writer = new FileWriter(jsonFilePathName);
            bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(jsonResponse);
            bufferedWriter.close();
        } catch (Exception exception) {
            log.error("Exception occured while writing json file={}: {}", jsonFilePathName, exception);
            throw exception;

        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }

    public static StringSubstitutor getStringSubstitutor(String invoiceId, String pageSize, String usageType) {

        Map<String, String> templateTokens = new HashMap<>();
        templateTokens.put(INVOICE_ID_TOKEN, invoiceId);
        templateTokens.put(SIZE_TOKEN, pageSize);
        templateTokens.put(USAGE_TYPE_TOKEN, usageType);

        return new StringSubstitutor(templateTokens);
    }
}
