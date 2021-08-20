package com.appdirect.iaas.azure.mockutility.model;

import lombok.Data;

@Data
public class WireMockMappingRequest {
    private String method = "GET";
    private String url;
}
