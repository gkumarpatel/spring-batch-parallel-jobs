package com.appdirect.iaas.azure.mockutility.model;

import java.util.List;

import lombok.Data;

@Data
public class DailyRatedUsageItemsResponse extends PartnerCenterAPIResponse {
    private List<DailyRatedUsageLineItemBean> list;
}
