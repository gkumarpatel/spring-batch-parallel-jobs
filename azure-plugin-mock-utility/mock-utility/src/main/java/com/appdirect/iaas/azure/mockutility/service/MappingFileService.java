package com.appdirect.iaas.azure.mockutility.service;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface MappingFileService {
    
    void generateOneTimeInvoiceMappingFile(ObjectMapper objectMapper, int oneTimeJsonFileCount, String invoiceId, String pageSize, String oneTimeMappingFilesPath) throws IOException;

    void generateDailyRatedUsageMappingFile(ObjectMapper objectMapper, int dailyRatedJsonFileCount, String invoiceId, String pageSize, String dailyRatedMappingFilesPath) throws IOException;
}
