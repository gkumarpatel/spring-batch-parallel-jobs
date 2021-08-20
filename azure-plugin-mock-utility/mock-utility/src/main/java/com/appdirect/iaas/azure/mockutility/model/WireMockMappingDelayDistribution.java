package com.appdirect.iaas.azure.mockutility.model;

import lombok.Data;

@Data
public class WireMockMappingDelayDistribution {
    private String type;
    private int median;
    private float sigma;
}
