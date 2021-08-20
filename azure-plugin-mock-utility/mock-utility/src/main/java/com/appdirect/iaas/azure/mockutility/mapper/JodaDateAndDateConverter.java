package com.appdirect.iaas.azure.mockutility.mapper;

import java.util.Date;

import org.joda.time.DateTime;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class JodaDateAndDateConverter extends BidirectionalConverter<DateTime, Date> {
  
    @Override
    public Date convertTo(DateTime source, Type<Date> destinationType) {
        return source.toDate();
    }

    @Override
    public DateTime convertFrom(Date source, Type<DateTime> destinationType) {
        return new DateTime(source);
    }
}
