package com.appdirect.iaas.azure.mockutility.service;

import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.DAILY_RATED_JSON_REPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.DAILY_RATED_MAPPING_JSON_REPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.JSON_FILE_EXTENTION;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.MS_CONTINUATION_TOKEN;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.ONE_TIME_JSON_RESPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.ONE_TIME_MAPPING_JSON_RESPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.USAGE_TYPE_DAILY;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.USAGE_TYPE_ONE_TIME;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.V1_API_PREFIX;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.nextResourceLinkURITemplate;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.selfResourceLinkURITemplate;
import static com.appdirect.iaas.azure.mockutility.util.FileUtil.getStringSubstitutor;
import static com.appdirect.iaas.azure.mockutility.util.FileUtil.writeResponseToJsonFile;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.appdirect.iaas.azure.mockutility.model.WireMockMapping;
import com.appdirect.iaas.azure.mockutility.model.WireMockMappingDelayDistribution;
import com.appdirect.iaas.azure.mockutility.model.WireMockMappingRequest;
import com.appdirect.iaas.azure.mockutility.model.WireMockMappingRequestHeader;
import com.appdirect.iaas.azure.mockutility.model.WireMockMappingResponse;
import com.appdirect.iaas.azure.mockutility.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class MappingFileServiceImpl implements MappingFileService {

    public static final String LINE_ITEM = "line-item";
    private static String oneTimeMappingFilesPath;
    private static String dailyRatedMappingFilesPath;

    @Value("${wireMock.delayDistribution.type}")
    private String delayDetributionType;

    @Value("${wireMock.delayDistribution.median}")
    private Integer delayDetributionMedian;

    @Value("${wireMock.delayDistribution.sigma}")
    private Float delayDetributionSigma;

    @Value("${wireMock.response.teamName}")
    private String teamName;

    @Value("${responseFolder.dailyRatedPath.mappingPath}")
    public String dailyRatedMappingsPath;

    @Value("${responseFolder.OneTimePath.mappingPath}")
    public String oneTimeMappingsPath;

    @Value("${mock.numberOfLineItems}")
    private Long numberOfLineItems;

    @Value("${responseFolder.mainFolderPath}")
    private String responseOutputPath;

    @Value("${wireMock.response.useCaseName}")
    private String useCaseNamePrefix;

    @PostConstruct
    public void setUp() {

        String totalInvoices = numberOfLineItems.toString();
        oneTimeMappingFilesPath = FileUtil.generateFolders(oneTimeMappingsPath, responseOutputPath, totalInvoices).toString();
        dailyRatedMappingFilesPath = FileUtil.generateFolders(dailyRatedMappingsPath, responseOutputPath, totalInvoices).toString();
    }

    @Override
    public void generateOneTimeInvoiceMappingFile(ObjectMapper objectMapper, int oneTimeJsonFileCount, String invoiceId, String pageSize, boolean isFirstResponse, String lastContinuationToken) throws IOException {

        String oneTimeResponseFileName = invoiceId.concat(ONE_TIME_JSON_RESPONSE_FILE).concat(String.valueOf(oneTimeJsonFileCount)).concat(".json");

        WireMockMappingResponse wireMockMappingResponse = getWireMockMappingResponse(oneTimeResponseFileName);

        StringSubstitutor stringSubstitutor = getStringSubstitutor(invoiceId, pageSize, USAGE_TYPE_ONE_TIME);
        String requestUrl = teamName.concat("/").concat(useCaseNamePrefix).concat(V1_API_PREFIX).concat(getRequestUrl(stringSubstitutor, isFirstResponse));

        WireMockMappingRequest wireMockMappingRequest = getWireMockMappingRequest(isFirstResponse, lastContinuationToken, requestUrl);
        WireMockMapping wireMockMapping = getWireMockMapping(wireMockMappingResponse, wireMockMappingRequest);

        String mappingFileJsonResponse = objectMapper.writeValueAsString(wireMockMapping);
        String mappingJsonFilePath = oneTimeMappingFilesPath.concat("/").concat(invoiceId).concat(ONE_TIME_MAPPING_JSON_RESPONSE_FILE).concat(String.valueOf(oneTimeJsonFileCount)).concat(JSON_FILE_EXTENTION);

        writeResponseToJsonFile(mappingJsonFilePath, mappingFileJsonResponse);
    }

    @Override
    public void generateDailyRatedUsageMappingFile(ObjectMapper objectMapper, int dailyRatedJsonFileCount, String invoiceId, String pageSize, boolean isFirstResponse, String lastContinuationToken) throws IOException {

        String dailyRatedResponseFileName = invoiceId.concat(DAILY_RATED_JSON_REPONSE_FILE).concat(String.valueOf(dailyRatedJsonFileCount)).concat(JSON_FILE_EXTENTION);

        WireMockMappingResponse wireMockMappingResponse = getWireMockMappingResponse(dailyRatedResponseFileName);

        StringSubstitutor stringSubstitutor = getStringSubstitutor(invoiceId, pageSize, USAGE_TYPE_DAILY);
        String requestUrl = teamName.concat("/").concat(useCaseNamePrefix).concat(V1_API_PREFIX).concat(getRequestUrl(stringSubstitutor, isFirstResponse));

        WireMockMappingRequest wireMockMappingRequest = getWireMockMappingRequest(isFirstResponse, lastContinuationToken, requestUrl);
        WireMockMapping wireMockMapping = getWireMockMapping(wireMockMappingResponse, wireMockMappingRequest);

        String mappingFileJsonResponse = objectMapper.writeValueAsString(wireMockMapping);
        String mappingJsonFilePath = (dailyRatedMappingFilesPath).concat("/").concat(invoiceId).concat(DAILY_RATED_MAPPING_JSON_REPONSE_FILE).concat(String.valueOf(dailyRatedJsonFileCount)).concat(JSON_FILE_EXTENTION);

        writeResponseToJsonFile(mappingJsonFilePath, mappingFileJsonResponse);
    }

    private WireMockMappingResponse getWireMockMappingResponse(String dailyRatedResponseFileName) {
        WireMockMappingDelayDistribution delayDistribution = getDelayDistribution();
        return getWireMockMappingResponse(dailyRatedResponseFileName, delayDistribution);
    }

    private String getRequestUrl(StringSubstitutor stringSubstitutor, boolean isFirstResponse) {
        if (isFirstResponse) {
            return stringSubstitutor.replace(selfResourceLinkURITemplate);
        } else {
            return stringSubstitutor.replace(nextResourceLinkURITemplate);
        }
    }

    private WireMockMappingDelayDistribution getDelayDistribution() {
        WireMockMappingDelayDistribution delayDistribution = new WireMockMappingDelayDistribution();
        delayDistribution.setMedian(delayDetributionMedian);
        delayDistribution.setSigma(delayDetributionSigma);
        delayDistribution.setType(delayDetributionType);
        return delayDistribution;
    }

    private WireMockMappingResponse getWireMockMappingResponse(String oneTimeResponseFileName, WireMockMappingDelayDistribution delayDistribution) {
        WireMockMappingResponse wireMockMappingResponse = new WireMockMappingResponse();
        wireMockMappingResponse.setDelayDistribution(delayDistribution);
        String bodyFileName = teamName.concat("/").concat(useCaseNamePrefix).concat("/").concat(LINE_ITEM).concat("/").concat(oneTimeResponseFileName);
        wireMockMappingResponse.setBodyFileName(bodyFileName);
        return wireMockMappingResponse;
    }

    private WireMockMappingRequest getWireMockMappingRequest(boolean isFirstResponse, String lastContinuationToken, String requestUrl) {
        WireMockMappingRequest wireMockMappingRequest = new WireMockMappingRequest();
        wireMockMappingRequest.setUrl(requestUrl);

        if (!isFirstResponse) {
            WireMockMappingRequestHeader continuationTokenHeader = new WireMockMappingRequestHeader();
            continuationTokenHeader.setContains(lastContinuationToken);
            continuationTokenHeader.setContains(lastContinuationToken);
            wireMockMappingRequest.setHeaders(Collections.singletonMap(MS_CONTINUATION_TOKEN, continuationTokenHeader));
        }
        return wireMockMappingRequest;
    }

    private WireMockMapping getWireMockMapping(WireMockMappingResponse wireMockMappingResponse, WireMockMappingRequest wireMockMappingRequest) {
        WireMockMapping wireMockMapping = new WireMockMapping();
        wireMockMapping.setRequest(wireMockMappingRequest);
        wireMockMapping.setResponse(wireMockMappingResponse);
        return wireMockMapping;
    }
}
