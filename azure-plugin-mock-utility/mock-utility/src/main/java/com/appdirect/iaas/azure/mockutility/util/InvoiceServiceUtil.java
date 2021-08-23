package com.appdirect.iaas.azure.mockutility.util;

import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.HTTP_METHOD_GET;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.MS_CONTINUATION_TOKEN;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.NEXT_RESOURCE_LINK;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.SELF_RESOURCE_LINK;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.nextResourceLinkURITemplate;
import static com.appdirect.iaas.azure.mockutility.constants.JobConstants.selfResourceLinkURITemplate;
import static com.appdirect.iaas.azure.mockutility.util.FileUtil.getStringSubstitutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.StringSubstitutor;

import com.appdirect.iaas.azure.mockutility.model.ResourceLink;
import com.appdirect.iaas.azure.mockutility.model.ResourceLinkHeader;

public class InvoiceServiceUtil {
    
    public static String generateContinuationToken(boolean isLastResponse) {
        String continuationToken = null;
        if (!isLastResponse) {
            continuationToken = RandomStringUtils.random(200, true, true);
        }
        return continuationToken;
    }

    public static Map<String, ResourceLink> getLinks(boolean isLastResponse, String continuationToken, String usageType, String invoiceId, String pageSize) {

        StringSubstitutor stringSubstitutor = getStringSubstitutor(invoiceId, pageSize, usageType);
        Map<String, ResourceLink> links = new HashMap<>();

        if (!isLastResponse) {
            links.put(NEXT_RESOURCE_LINK, generateNextResourceLink(continuationToken, stringSubstitutor));
        }

        links.put(SELF_RESOURCE_LINK, generateSelfLink(stringSubstitutor));

        return links;
    }

    public static ResourceLink generateSelfLink(StringSubstitutor stringSubstitutor) {
        ResourceLink selfLink = new ResourceLink();
        List<ResourceLinkHeader> headers = Collections.emptyList();

        selfLink.setHeaders(headers);
        selfLink.setUri(stringSubstitutor.replace(selfResourceLinkURITemplate));
        selfLink.setMethod(HTTP_METHOD_GET);
        return selfLink;
    }

    public static ResourceLink generateNextResourceLink(String continuationToken, StringSubstitutor stringSubstitutor) {
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
