package com.appdirect.iaas.azure.mockutility.model;

import java.util.Collections;
import java.util.Map;

import lombok.Data;

@Data
public class WireMockMappingResponse {
    private String status = "200";
    private WireMockMappingDelayDistribution delayDistribution;
    private String bodyFileName;
    private Map<String, String> headers = Collections.singletonMap("Content-Type", "application/json");
}
