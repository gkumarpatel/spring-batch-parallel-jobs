package com.appdirect.iaas.azure.mockutility.constants;

public class JobConstants {
    public static final String selfResourceLinkURITemplate = "/invoices/${invoiceId}/lineitems?provider=onetime&invoicelineitemtype=${usageType}&size=${size}";
    public static final String nextResourceLinkURITemplate = "/invoices/${invoiceId}/lineitems?provider=onetime&invoicelineitemtype=${usageType}&size=${size}&seekOperation=Next";
    public static final String USAGE_TYPE_DAILY = "usagelineitems";
    public static final String USAGE_TYPE_ONE_TIME = "billinglineitems";
    public static final String ONE_TIME_JSON_RESPONSE_FILE = "OneTimeResponse_";
    public static final String DAILY_RATED_JSON_REPONSE_FILE = "DailyRated_";
    public static final String ONE_TIME_MAPPING_JSON_RESPONSE_FILE = "OneTimeMapping_";
    public static final String DAILY_RATED_MAPPING_JSON_REPONSE_FILE = "DailyMapping_";
    public static final String JSON_FILE_EXTENTION = ".json";
    public static final String MS_CONTINUATION_TOKEN = "MS-ContinuationToken";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String NEXT_RESOURCE_LINK = "next";
    public static final String SELF_RESOURCE_LINK = "self";
    public static final String INVOICE_ID_TOKEN = "invoiceId";
    public static final String SIZE_TOKEN = "size";
    public static final String USAGE_TYPE_TOKEN = "usageType";
    public static final String V1_API_PREFIX = "/v1";
}
