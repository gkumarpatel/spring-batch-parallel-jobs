package com.appdirect.iaas.azure.mockutility.service;

import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.DAILY_RATED_JSON_REPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.JSON_FILE_EXTENTION;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.ONE_TIME_JSON_RESPONSE_FILE;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.USAGE_TYPE_DAILY;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.USAGE_TYPE_ONE_TIME;
import static com.appdirect.iaas.azure.mockutility.util.FileUtil.writeResponseToJsonFile;
import static com.appdirect.iaas.azure.mockutility.util.InvoiceServiceUtil.generateContinuationToken;
import static com.appdirect.iaas.azure.mockutility.util.InvoiceServiceUtil.getLinks;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.appdirect.iaas.azure.mockutility.mapper.DailyRatedUsageLineItemMapper;
import com.appdirect.iaas.azure.mockutility.mapper.OneTimeInvoiceLineItemMapper;
import com.appdirect.iaas.azure.mockutility.model.DailyRatedUsageItemsResponse;
import com.appdirect.iaas.azure.mockutility.model.DailyRatedUsageLineItemBean;
import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemBean;
import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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


    @Override
    public void generateOneTimeInvoiceResponseFile(ObjectMapper objectMapper, boolean isLastResponse, List<InvoiceLineItem> invoiceLineItems, String oneTimeInvoiceFilesPath, int oneTimeJsonFileCount) throws IOException {

        InvoiceLineItem invoiceLineItem = invoiceLineItems.get(0);
        String pageSize = String.valueOf(invoiceLineItems.size());

        String invoiceNumber = ((OneTimeInvoiceLineItem) invoiceLineItem).getInvoiceNumber();
        OneTimeInvoiceLineItemResponse oneTimeInvoiceLineItemResponse = new OneTimeInvoiceLineItemResponse();
        String continuationToken = null;

        List<OneTimeInvoiceLineItemBean> oneTimeInvoiceLineItemBeans = invoiceLineItems.stream().map(item -> oneTimeInvoiceLineItemMapper.mapFromOneTimeInvoiceLineItem((OneTimeInvoiceLineItem) item)
        ).collect(Collectors.toList());

        oneTimeInvoiceLineItemResponse.setItems(oneTimeInvoiceLineItemBeans);
        oneTimeInvoiceLineItemResponse.setTotalCount(oneTimeInvoiceLineItemBeans.size());

        continuationToken = generateContinuationToken(isLastResponse);
        oneTimeInvoiceLineItemResponse.setContinuationToken(continuationToken);

        oneTimeInvoiceLineItemResponse.setLinks(getLinks(isLastResponse, continuationToken, USAGE_TYPE_ONE_TIME, invoiceNumber, pageSize));

        String oneTimeResponseFileName = ONE_TIME_JSON_RESPONSE_FILE.concat(String.valueOf(oneTimeJsonFileCount)).concat(".json");
        String invoiceLineJsonFilePathName = oneTimeInvoiceFilesPath.concat("/").concat(oneTimeResponseFileName);

        //TODO : Seperat it to file utils
        String invoiceFileJsonResponse = null;
        try {
            invoiceFileJsonResponse = objectMapper.writeValueAsString(oneTimeInvoiceLineItemResponse);
        } catch (JsonProcessingException exception) {
            log.error("Error occured while converting object to json={}", exception);
            throw exception;
        }

        writeResponseToJsonFile(invoiceLineJsonFilePathName, invoiceFileJsonResponse);
    }

    @Override
    public void generateDailyRatedUsageResponseFile(ObjectMapper objectMapper, boolean isLastResponse, List<InvoiceLineItem> invoiceLineItems, String dailyRatedInvoiceFilesPath, int dailyRatedJsonFileCount) throws IOException {

        InvoiceLineItem invoiceLineItem = invoiceLineItems.get(0);
        String pageSize = String.valueOf(invoiceLineItems.size());
        
        String invoiceNumber = ((DailyRatedUsageLineItem) invoiceLineItem).getInvoiceNumber();
        DailyRatedUsageItemsResponse dailyRatedUsageItemsResponse = new DailyRatedUsageItemsResponse();
        String continuationToken = null;

        List<DailyRatedUsageLineItemBean> dailyRatedUsageLineItemBeans = invoiceLineItems.stream().map(item -> dailyRatedUsageLineItemMapper.mapFromDailyRatedInvoiceLineItem((DailyRatedUsageLineItem) item))
                .collect(Collectors.toList());
        dailyRatedUsageItemsResponse.setItems(dailyRatedUsageLineItemBeans);
        dailyRatedUsageItemsResponse.setTotalCount(dailyRatedUsageLineItemBeans.size());

        continuationToken = generateContinuationToken(isLastResponse);

        dailyRatedUsageItemsResponse.setContinuationToken(continuationToken);

        dailyRatedUsageItemsResponse.setLinks(getLinks(isLastResponse, continuationToken, USAGE_TYPE_DAILY, invoiceNumber, pageSize));

        String dailyRatedResponseFileName  = DAILY_RATED_JSON_REPONSE_FILE.concat(String.valueOf(dailyRatedJsonFileCount)).concat(JSON_FILE_EXTENTION);
        String invoiceLineJsonFilePathName = dailyRatedInvoiceFilesPath.concat("/").concat(dailyRatedResponseFileName);

        //TODO : Seperat it to file utils
        String invoiceFileJsonResponse = null;
        try {
            invoiceFileJsonResponse = objectMapper.writeValueAsString(dailyRatedUsageItemsResponse);
        } catch (JsonProcessingException exception) {
            log.error("Error occured while converting object to json={}", exception);
        }

        writeResponseToJsonFile(invoiceLineJsonFilePathName, invoiceFileJsonResponse);
        
    }
}
