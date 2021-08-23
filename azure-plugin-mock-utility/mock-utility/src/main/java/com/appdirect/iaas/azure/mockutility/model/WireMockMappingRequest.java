package com.appdirect.iaas.azure.mockutility.model;

import java.util.Map;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
public class WireMockMappingRequest {
    private String method = "GET";
    private String url;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, WireMockMappingRequestHeader> headers = null;
}
