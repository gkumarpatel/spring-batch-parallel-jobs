package com.appdirect.iaas.azure.mockutility.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

public class StringToMapConverter extends CustomConverter<String, Map<String, Object>> {

    @Override
    public Map<String, Object> convert(String source, Type<? extends Map<String, Object>> destinationType) {
        Map<String, Object> propertyMap = new HashMap<>();
        String[] properties = source.split(",");
        
        if (properties.length > 0) {
            Arrays.stream(properties).forEach(property -> {
                if(StringUtils.isNotEmpty(property) && property.contains(":")) {
                    String[] keyAndValues = property.split(":");
                    propertyMap.put(keyAndValues[0], keyAndValues[1]);
                }
            });
        }
        return propertyMap;
    }
}
