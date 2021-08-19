package com.appdirect.iaas.azure.mockutility.batch;

import java.io.File;
import java.io.FileWriter;
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

    private static Long lineItemsToWrite;
    private static int oneTimeJsonFileCount = 1;
    private static int dailyRatedJsonFileCount = 1;

    private final OneTimeInvoiceLineItemMapper oneTimeInvoiceLineItemMapper;
    private final DailyRatedUsageLineItemMapper dailyRatedUsageLineItemMapper;

    @Value("${mock.numberOfLineItems}")
    private Long numberOfLineItems;

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

            if (!isLastResponse) {
                continuationToken = RandomStringUtils.random(200, true, true);
            }
            oneTimeInvoiceLineItemResponse.setContinuationToken(continuationToken);
            oneTimeInvoiceLineItemResponse.setLinks(getLinks(isLastResponse, continuationToken, "billinglineitems", ((OneTimeInvoiceLineItem) invoiceLineItem).getInvoiceNumber(), String.valueOf(items.size())));

            String oneTimeFolderPath =  System.getProperty("user.home").concat("/OneTime");
            String jsonPath = "OneTimeResponse_".concat(String.valueOf(dailyRatedJsonFileCount++)).concat(".json");
            gson.toJson(oneTimeInvoiceLineItemResponse, new FileWriter(new File(oneTimeFolderPath, jsonPath)));

        } else {

            DailyRatedUsageItemsResponse dailyRatedUsageItemsResponse = new DailyRatedUsageItemsResponse();
            String continuationToken = null;

            List<DailyRatedUsageLineItemBean> dailyRatedUsageLineItemBeans = items.stream().map(item -> dailyRatedUsageLineItemMapper.mapFromDailyRatedInvoiceLineItem((DailyRatedUsageLineItem) item))
                    .collect(Collectors.toList());
            dailyRatedUsageItemsResponse.setList(dailyRatedUsageLineItemBeans);
            dailyRatedUsageItemsResponse.setTotalCount(dailyRatedUsageLineItemBeans.size());

            if (!isLastResponse) {
                continuationToken = RandomStringUtils.random(200, true, true);
            }

            dailyRatedUsageItemsResponse.setContinuationToken(continuationToken);
            dailyRatedUsageItemsResponse.setLinks(getLinks(isLastResponse, continuationToken, "usagelineitems", ((DailyRatedUsageLineItem) invoiceLineItem).getInvoiceNumber(), String.valueOf(items.size())));

            String dailyRatedFolderPath =  System.getProperty("user.home").concat("/DailyRated");
            String jsonPath = "DailyRatedResponse_".concat(String.valueOf(dailyRatedJsonFileCount++)).concat(".json");
            gson.toJson(dailyRatedUsageItemsResponse, new FileWriter(new File(dailyRatedFolderPath, jsonPath)));
        }

        lineItemsToWrite -= items.size();
    }

    private Map<String, ResourceLink> getLinks(boolean isLastResponse, String continuationToken, String usageType, String invoiceId, String pageSize) {
        Map<String, ResourceLink> links = new HashMap<>();
        Map<String, String> templateTokens = new HashMap<>();
        templateTokens.put("invoiceId", invoiceId);
        templateTokens.put("size", pageSize);
        templateTokens.put("usageType", usageType);

        StringSubstitutor stringSubstitutor = new StringSubstitutor(templateTokens);

        if (!isLastResponse) {
            ResourceLink nextLink = new ResourceLink();
            List<ResourceLinkHeader> headers = new ArrayList<>();
            ResourceLinkHeader nextLinkHeader = new ResourceLinkHeader();
            nextLinkHeader.setKey("MS-ContinuationToken");
            nextLinkHeader.setValue(continuationToken);
            headers.add(nextLinkHeader);
            nextLink.setHeaders(headers);
            nextLink.setMethod("GET");

            nextLink.setUri(stringSubstitutor.replace(nextResourceLinkURITemplate));

            links.put("next", nextLink);
        }

        ResourceLink selfLink = new ResourceLink();
        List<ResourceLinkHeader> headers = Collections.emptyList();

        selfLink.setHeaders(headers);
        selfLink.setUri(stringSubstitutor.replace(selfResourceLinkURITemplate));
        selfLink.setMethod("GET");

        links.put("self", selfLink);

        return links;
    }
}
