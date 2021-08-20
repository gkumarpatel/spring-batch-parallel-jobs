package com.appdirect.iaas.azure.mockutility.model;

import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceLink {
    private String uri;
    private String method;
    private List<ResourceLinkHeader> headers;
}
