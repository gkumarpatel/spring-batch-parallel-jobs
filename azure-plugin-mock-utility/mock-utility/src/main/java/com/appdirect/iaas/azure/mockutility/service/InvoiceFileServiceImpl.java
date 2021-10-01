package com.appdirect.iaas.azure.mockutility.service;

import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.DAILY_RATED_JSON_REPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.JSON_FILE_EXTENTION;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.ONE_TIME_JSON_RESPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.USAGE_TYPE_DAILY;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.USAGE_TYPE_ONE_TIME;
import static com.appdirect.iaas.azure.mockutility.util.FileUtil.convertObjectToJson;
import static com.appdirect.iaas.azure.mockutility.util.FileUtil.writeResponseToJsonFile;
import static com.appdirect.iaas.azure.mockutility.util.InvoiceServiceUtil.generateContinuationToken;
import static com.appdirect.iaas.azure.mockutility.util.InvoiceServiceUtil.getLinks;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.appdirect.iaas.azure.mockutility.mapper.DailyRatedUsageLineItemMapper;
import com.appdirect.iaas.azure.mockutility.mapper.OneTimeInvoiceLineItemMapper;
import com.appdirect.iaas.azure.mockutility.model.DailyRatedUsageItemsResponse;
import com.appdirect.iaas.azure.mockutility.model.DailyRatedUsageLineItemBean;
import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemBean;
import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemResponse;
import com.appdirect.iaas.azure.mockutility.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.store.partnercenter.models.invoices.DailyRatedUsageLineItem;
import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItem;
import com.microsoft.store.partnercenter.models.invoices.OneTimeInvoiceLineItem;

@RequiredArgsConstructor
@Service
@Slf4j
public class InvoiceFileServiceImpl implements InvoiceFileService {

    private final OneTimeInvoiceLineItemMapper oneTimeInvoiceLineItemMapper;
    private final DailyRatedUsageLineItemMapper dailyRatedUsageLineItemMapper;
    
    private static String oneTimeInvoiceFilesPath;
    private static String dailyRatedInvoiceFilesPath;

    @Value("${responseFolder.mainFolderPath}")
    private String responseOutputPath;

    @Value("${mock.numberOfLineItems}")
    private Long numberOfLineItems;

    @Value("${responseFolder.dailyRatedPath.filesPath}")
    public String dailyRatedFilesPath;

    @Value("${responseFolder.OneTimePath.filesPath}")
    public String oneTimeFilesPath;
    
    @PostConstruct
    public void setUp() {
        String totalInvoices = numberOfLineItems.toString();
        oneTimeInvoiceFilesPath = FileUtil.generateFolders(oneTimeFilesPath, responseOutputPath, totalInvoices).toString();
        dailyRatedInvoiceFilesPath = FileUtil.generateFolders(dailyRatedFilesPath, responseOutputPath, totalInvoices).toString();
    }
    
    @Override
    public String generateOneTimeInvoiceResponseFile(ObjectMapper objectMapper, boolean isLastResponse, List<InvoiceLineItem> invoiceLineItems, int oneTimeJsonFileCount) throws IOException {

        InvoiceLineItem invoiceLineItem = invoiceLineItems.get(0);
        String pageSize = String.valueOf(invoiceLineItems.size());

        String invoiceNumber = ((OneTimeInvoiceLineItem) invoiceLineItem).getInvoiceNumber();
        OneTimeInvoiceLineItemResponse oneTimeInvoiceLineItemResponse = new OneTimeInvoiceLineItemResponse();

        List<OneTimeInvoiceLineItemBean> oneTimeInvoiceLineItemBeans = invoiceLineItems.stream().map(item -> oneTimeInvoiceLineItemMapper.mapFromOneTimeInvoiceLineItem((OneTimeInvoiceLineItem) item)
        ).collect(Collectors.toList());

        oneTimeInvoiceLineItemResponse.setItems(oneTimeInvoiceLineItemBeans);
        oneTimeInvoiceLineItemResponse.setTotalCount(oneTimeInvoiceLineItemBeans.size());

        String continuationToken = null;
        continuationToken = generateContinuationToken(isLastResponse);
        oneTimeInvoiceLineItemResponse.setContinuationToken(continuationToken);

        oneTimeInvoiceLineItemResponse.setLinks(getLinks(isLastResponse, continuationToken, USAGE_TYPE_ONE_TIME, invoiceNumber, pageSize));
        String invoiceFileJsonResponse = convertObjectToJson(objectMapper, oneTimeInvoiceLineItemResponse);
       
        String oneTimeResponseFileName = invoiceNumber.concat(ONE_TIME_JSON_RESPONSE_FILE).concat(String.valueOf(oneTimeJsonFileCount)).concat(".json");
        String invoiceLineJsonFilePathName = oneTimeInvoiceFilesPath.concat("/").concat(oneTimeResponseFileName);
        writeResponseToJsonFile(invoiceLineJsonFilePathName, invoiceFileJsonResponse);
        return continuationToken;
    }

    @Override
    public String generateDailyRatedUsageResponseFile(ObjectMapper objectMapper, boolean isLastResponse, List<InvoiceLineItem> invoiceLineItems, int dailyRatedJsonFileCount) throws IOException {

        InvoiceLineItem invoiceLineItem = invoiceLineItems.get(0);
        String pageSize = String.valueOf(invoiceLineItems.size());
        
        String invoiceNumber = ((DailyRatedUsageLineItem) invoiceLineItem).getInvoiceNumber();
        DailyRatedUsageItemsResponse dailyRatedUsageItemsResponse = new DailyRatedUsageItemsResponse();

        List<DailyRatedUsageLineItemBean> dailyRatedUsageLineItemBeans = invoiceLineItems.stream().map(item -> dailyRatedUsageLineItemMapper.mapFromDailyRatedInvoiceLineItem((DailyRatedUsageLineItem) item))
                .collect(Collectors.toList());
        dailyRatedUsageItemsResponse.setItems(dailyRatedUsageLineItemBeans);
        dailyRatedUsageItemsResponse.setTotalCount(dailyRatedUsageLineItemBeans.size());

        String continuationToken = null;
        continuationToken = generateContinuationToken(isLastResponse);
        dailyRatedUsageItemsResponse.setContinuationToken(continuationToken);
        
        dailyRatedUsageItemsResponse.setLinks(getLinks(isLastResponse, continuationToken, USAGE_TYPE_DAILY, invoiceNumber, pageSize));
        String invoiceFileJsonResponse = convertObjectToJson(objectMapper, dailyRatedUsageItemsResponse);
        
        String dailyRatedResponseFileName  = invoiceNumber.concat(DAILY_RATED_JSON_REPONSE_FILE).concat(String.valueOf(dailyRatedJsonFileCount)).concat(JSON_FILE_EXTENTION);
        String invoiceLineJsonFilePathName = dailyRatedInvoiceFilesPath.concat("/").concat(dailyRatedResponseFileName);
        writeResponseToJsonFile(invoiceLineJsonFilePathName, invoiceFileJsonResponse);
        return continuationToken;
    }
}
