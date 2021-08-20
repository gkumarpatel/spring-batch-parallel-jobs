package com.appdirect.iaas.azure.mockutility.batch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.appdirect.iaas.azure.mockutility.mapper.DailyRatedUsageLineItemMapper;
import com.appdirect.iaas.azure.mockutility.mapper.OneTimeInvoiceLineItemMapper;
import com.appdirect.iaas.azure.mockutility.model.DailyRatedUsageItemsResponse;
import com.appdirect.iaas.azure.mockutility.model.DailyRatedUsageLineItemBean;
import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemBean;
import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemResponse;
import com.appdirect.iaas.azure.mockutility.model.ResourceLink;
import com.appdirect.iaas.azure.mockutility.model.ResourceLinkHeader;
import com.google.gson.Gson;
import com.microsoft.store.partnercenter.models.invoices.DailyRatedUsageLineItem;
import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItem;
import com.microsoft.store.partnercenter.models.invoices.OneTimeInvoiceLineItem;

@Component
@Slf4j
@RequiredArgsConstructor
public class InvoiceItemWriter implements ItemWriter<InvoiceLineItem> {

    private static final String selfResourceLinkURITemplate = "/invoices/${invoiceId}/lineitems?provider=OneTime&invoicelineitemtype=${usageType}&size=${size}";
    private static final String nextResourceLinkURITemplate = "/invoices/${invoiceId}/lineitems?provider=OneTime&invoicelineitemtype=${usageType}&size=${size}&seekOperation=Next";
    public static final String USAGE_TYPE_DAILY = "usagelineitems";
    public static final String USAGE_TYPE_ONE_TIME = "billinglineitems";
    public static final String ONE_TIME_JSON_RESPONSE_FILE = "OneTimeResponse_";
    public static final String DAILY_RATED_JSON_REPONSE_FILE = "DailyRated_";
    public static final String JSON_FILE_EXTENTION = ".json";
    public static final String MS_CONTINUATION_TOKEN = "MS-ContinuationToken";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String NEXT_RESOURCE_LINK = "next";
    public static final String SELF_RESOURCE_LINK = "self";
    public static final String INVOICE_ID_TOKEN = "invoiceId";
    public static final String SIZE_TOKEN = "size";
    public static final String USAGE_TYPE_TOKEN = "usageType";

    private static Long lineItemsToWrite;
    private static int oneTimeJsonFileCount = 1;
    private static int dailyRatedJsonFileCount = 1;

    private final OneTimeInvoiceLineItemMapper oneTimeInvoiceLineItemMapper;
    private final DailyRatedUsageLineItemMapper dailyRatedUsageLineItemMapper;

    @Value("${mock.numberOfLineItems}")
    private Long numberOfLineItems;

    @Value("${apiResponsePath.OneTime}")
    private String oneTimeResponsePath;

    @Value("${apiResponsePath.DailyRated}")
    private String dailyRatedResponsePath;

    @PostConstruct
    public void setUp() {
        lineItemsToWrite = numberOfLineItems;
    }

    @Override
    public void write(List<? extends InvoiceLineItem> items) throws Exception {
        Gson gson = new Gson();

        boolean isLastResponse = false;

        if (lineItemsToWrite <= (2 * items.size())) {
            isLastResponse = true;
        }
        InvoiceLineItem invoiceLineItem = items.get(0);

        if (invoiceLineItem instanceof OneTimeInvoiceLineItem) {
            OneTimeInvoiceLineItemResponse oneTimeInvoiceLineItemResponse = new OneTimeInvoiceLineItemResponse();
            String continuationToken = null;

            List<OneTimeInvoiceLineItemBean> oneTimeInvoiceLineItemBeans = items.stream().map(item -> oneTimeInvoiceLineItemMapper.mapFromOneTimeInvoiceLineItem((OneTimeInvoiceLineItem) item)
            ).collect(Collectors.toList());

            oneTimeInvoiceLineItemResponse.setList(oneTimeInvoiceLineItemBeans);
            oneTimeInvoiceLineItemResponse.setTotalCount(oneTimeInvoiceLineItemBeans.size());

            continuationToken = generateContinuationToken(isLastResponse);
            oneTimeInvoiceLineItemResponse.setContinuationToken(continuationToken);
            oneTimeInvoiceLineItemResponse.setLinks(getLinks(isLastResponse, continuationToken, USAGE_TYPE_ONE_TIME, ((OneTimeInvoiceLineItem) invoiceLineItem).getInvoiceNumber(), String.valueOf(items.size())));

            String jsonFilePathName = oneTimeResponsePath.concat("/").concat(ONE_TIME_JSON_RESPONSE_FILE).concat(String.valueOf(oneTimeJsonFileCount++)).concat(".json");
            String jsonResponse = gson.toJson(oneTimeInvoiceLineItemResponse);

            writeResponseToJsonFile(jsonFilePathName, jsonResponse);

        } else {

            DailyRatedUsageItemsResponse dailyRatedUsageItemsResponse = new DailyRatedUsageItemsResponse();
            String continuationToken = null;

            List<DailyRatedUsageLineItemBean> dailyRatedUsageLineItemBeans = items.stream().map(item -> dailyRatedUsageLineItemMapper.mapFromDailyRatedInvoiceLineItem((DailyRatedUsageLineItem) item))
                    .collect(Collectors.toList());
            dailyRatedUsageItemsResponse.setList(dailyRatedUsageLineItemBeans);
            dailyRatedUsageItemsResponse.setTotalCount(dailyRatedUsageLineItemBeans.size());

            continuationToken = generateContinuationToken(isLastResponse);

            dailyRatedUsageItemsResponse.setContinuationToken(continuationToken);
            dailyRatedUsageItemsResponse.setLinks(getLinks(isLastResponse, continuationToken, USAGE_TYPE_DAILY, ((DailyRatedUsageLineItem) invoiceLineItem).getInvoiceNumber(), String.valueOf(items.size())));

            String jsonFilePathName = dailyRatedResponsePath.concat("/").concat(DAILY_RATED_JSON_REPONSE_FILE).concat(String.valueOf(dailyRatedJsonFileCount++)).concat(JSON_FILE_EXTENTION);
            String jsonResponse = gson.toJson(dailyRatedUsageItemsResponse);

            writeResponseToJsonFile(jsonFilePathName, jsonResponse);
        }

        lineItemsToWrite -= items.size();
    }

    private String generateContinuationToken(boolean isLastResponse) {
        String continuationToken = null;
        if (!isLastResponse) {
            continuationToken = RandomStringUtils.random(200, true, true);
        }
        return continuationToken;
    }

    private void writeResponseToJsonFile(String jsonFilePathName, String jsonResponse) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter writer = new FileWriter(jsonFilePathName);
            bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(jsonResponse);
            bufferedWriter.close();
        } catch (Exception e) {
            throw e;
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }

    private Map<String, ResourceLink> getLinks(boolean isLastResponse, String continuationToken, String usageType, String invoiceId, String pageSize) {
        Map<String, ResourceLink> links = new HashMap<>();
        Map<String, String> templateTokens = new HashMap<>();
        templateTokens.put(INVOICE_ID_TOKEN, invoiceId);
        templateTokens.put(SIZE_TOKEN, pageSize);
        templateTokens.put(USAGE_TYPE_TOKEN, usageType);

        StringSubstitutor stringSubstitutor = new StringSubstitutor(templateTokens);

        if (!isLastResponse) {
            links.put(NEXT_RESOURCE_LINK,  generateNextResourceLink(continuationToken, stringSubstitutor));
        }

        links.put(SELF_RESOURCE_LINK, generateSelfLink(stringSubstitutor));

        return links;
    }

    private ResourceLink generateSelfLink(StringSubstitutor stringSubstitutor) {
        ResourceLink selfLink = new ResourceLink();
        List<ResourceLinkHeader> headers = Collections.emptyList();

        selfLink.setHeaders(headers);
        selfLink.setUri(stringSubstitutor.replace(selfResourceLinkURITemplate));
        selfLink.setMethod(HTTP_METHOD_GET);
        return selfLink;
    }

    private ResourceLink generateNextResourceLink(String continuationToken, StringSubstitutor stringSubstitutor) {
        ResourceLink nextLink = new ResourceLink();

        ResourceLinkHeader nextLinkHeader = new ResourceLinkHeader();
        nextLinkHeader.setKey(MS_CONTINUATION_TOKEN);
        nextLinkHeader.setValue(continuationToken);

        List<ResourceLinkHeader> headers = new ArrayList<>();
        headers.add(nextLinkHeader);
        nextLink.setHeaders(headers);
        nextLink.setMethod(HTTP_METHOD_GET);

        nextLink.setUri(stringSubstitutor.replace(nextResourceLinkURITemplate));
        return nextLink;
    }
}
