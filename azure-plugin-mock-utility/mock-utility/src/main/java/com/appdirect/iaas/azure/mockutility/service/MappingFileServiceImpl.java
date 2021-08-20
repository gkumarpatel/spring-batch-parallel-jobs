package com.appdirect.iaas.azure.mockutility.service;

import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.DAILY_RATED_JSON_REPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.DAILY_RATED_MAPPING_JSON_REPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.JSON_FILE_EXTENTION;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.ONE_TIME_JSON_RESPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.ONE_TIME_MAPPING_JSON_RESPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.USAGE_TYPE_DAILY;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.USAGE_TYPE_ONE_TIME;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.selfResourceLinkURITemplate;
import static com.appdirect.iaas.azure.mockutility.util.FileUtil.getStringSubstitutor;
import static com.appdirect.iaas.azure.mockutility.util.FileUtil.writeResponseToJsonFile;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.appdirect.iaas.azure.mockutility.model.WireMockMapping;
import com.appdirect.iaas.azure.mockutility.model.WireMockMappingDelayDistribution;
import com.appdirect.iaas.azure.mockutility.model.WireMockMappingRequest;
import com.appdirect.iaas.azure.mockutility.model.WireMockMappingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class MappingFileServiceImpl implements MappingFileService {

    @Value("${wireMock.delayDistribution.type}")
    private String delayDetributionType;

    @Value("${wireMock.delayDistribution.median}")
    private Integer delayDetributionMedian;

    @Value("${wireMock.delayDistribution.sigma}")
    private Float delayDetributionSigma;

    @Value("${wireMock.response.bodyFileNamePrefix}")
    private String bodyFileNamePrefix;

    @Value("${responseFolder.dailyRatedPath.mappingPath}")
    public String dailyRatedMappingsPath;

    @Value("${responseFolder.OneTimePath.mappingPath}")
    public String oneTimeMappingsPath;

    @Override
    public void generateOneTimeInvoiceMappingFile(ObjectMapper objectMapper, int oneTimeJsonFileCount, String invoiceId, String pageSize, String oneTimeMappingFilesPath) throws IOException {

        String oneTimeResponseFileName = ONE_TIME_JSON_RESPONSE_FILE.concat(String.valueOf(oneTimeJsonFileCount)).concat(".json");

        WireMockMappingDelayDistribution delayDistribution = new WireMockMappingDelayDistribution();
        delayDistribution.setMedian(delayDetributionMedian);
        delayDistribution.setSigma(delayDetributionSigma);
        delayDistribution.setType(delayDetributionType);

        WireMockMappingResponse wireMockMappingResponse = new WireMockMappingResponse();
        wireMockMappingResponse.setDelayDistribution(delayDistribution);
        String bodyFileName = bodyFileNamePrefix.concat("/").concat(oneTimeResponseFileName);
        wireMockMappingResponse.setBodyFileName(bodyFileName);

        StringSubstitutor stringSubstitutor = getStringSubstitutor(invoiceId, pageSize, USAGE_TYPE_ONE_TIME);
        String requestUrl = stringSubstitutor.replace(selfResourceLinkURITemplate);

        WireMockMappingRequest wireMockMappingRequest = new WireMockMappingRequest();
        wireMockMappingRequest.setUrl(requestUrl);

        WireMockMapping wireMockMapping = new WireMockMapping();
        wireMockMapping.setRequest(wireMockMappingRequest);
        wireMockMapping.setResponse(wireMockMappingResponse);

        String mappingFileJsonResponse = objectMapper.writeValueAsString(wireMockMapping);
        String mappingJsonFilePath = oneTimeMappingFilesPath.concat("/").concat(ONE_TIME_MAPPING_JSON_RESPONSE_FILE).concat(String.valueOf(oneTimeJsonFileCount)).concat(JSON_FILE_EXTENTION);

        writeResponseToJsonFile(mappingJsonFilePath, mappingFileJsonResponse);
    }

    @Override
    public void generateDailyRatedUsageMappingFile(ObjectMapper objectMapper, int dailyRatedJsonFileCount, String invoiceId, String pageSize, String dailyRatedMappingFilesPath) throws IOException {

        String dailyRatedResponseFileName = DAILY_RATED_JSON_REPONSE_FILE.concat(String.valueOf(dailyRatedJsonFileCount)).concat(JSON_FILE_EXTENTION);

        WireMockMappingDelayDistribution delayDistribution = new WireMockMappingDelayDistribution();
        delayDistribution.setMedian(delayDetributionMedian);
        delayDistribution.setSigma(delayDetributionSigma);
        delayDistribution.setType(delayDetributionType);

        WireMockMappingResponse wireMockMappingResponse = new WireMockMappingResponse();
        wireMockMappingResponse.setDelayDistribution(delayDistribution);

        String bodyFileName = bodyFileNamePrefix.concat("/").concat(dailyRatedResponseFileName);
        wireMockMappingResponse.setBodyFileName(bodyFileName);

        StringSubstitutor stringSubstitutor = getStringSubstitutor(invoiceId, pageSize, USAGE_TYPE_DAILY);
        String requestUrl = stringSubstitutor.replace(selfResourceLinkURITemplate);

        WireMockMappingRequest wireMockMappingRequest = new WireMockMappingRequest();
        wireMockMappingRequest.setUrl(requestUrl);

        WireMockMapping wireMockMapping = new WireMockMapping();
        wireMockMapping.setRequest(wireMockMappingRequest);
        wireMockMapping.setResponse(wireMockMappingResponse);

        String mappingFileJsonResponse = objectMapper.writeValueAsString(wireMockMapping);
        String mappingJsonFilePath = dailyRatedMappingFilesPath.concat("/").concat(DAILY_RATED_MAPPING_JSON_REPONSE_FILE).concat(String.valueOf(dailyRatedJsonFileCount)).concat(JSON_FILE_EXTENTION);

        writeResponseToJsonFile(mappingJsonFilePath, mappingFileJsonResponse);
    }
}
