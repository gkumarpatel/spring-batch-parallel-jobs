package com.appdirect.iaas.azure.mockutility.model;

import java.util.Map;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
public class PartnerCenterAPIResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String continuationToken;
    private Integer totalCount;
    private Map<String, ResourceLink> links;
    private String objectType = "Collection";
}
