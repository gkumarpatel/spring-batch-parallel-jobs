package com.appdirect.iaas.azure.mockutility.mapper;

import java.time.LocalDate;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class LocalDateAndJodaDateConverter extends BidirectionalConverter<LocalDate, DateTime> {

    @Override
    public DateTime convertTo(LocalDate source, Type<DateTime> destinationType) {
        return new DateTime(DateTimeZone.UTC).withDate(
                source.getYear(), source.getMonthValue(), source.getDayOfMonth()
        ).withTime(0, 0, 0, 0);
    }

    @Override
    public LocalDate convertFrom(DateTime source, Type<LocalDate> destinationType) {
        DateTime dateTimeUtc = source.withZone(DateTimeZone.UTC);
        return LocalDate.of(dateTimeUtc.getYear(), dateTimeUtc.getMonthOfYear(), dateTimeUtc.getDayOfMonth());
    }
}
