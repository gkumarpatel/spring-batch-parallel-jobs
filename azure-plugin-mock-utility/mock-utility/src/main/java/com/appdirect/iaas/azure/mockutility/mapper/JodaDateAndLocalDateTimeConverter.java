package com.appdirect.iaas.azure.mockutility.mapper;

import java.time.LocalDateTime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class JodaDateAndLocalDateTimeConverter extends BidirectionalConverter<DateTime, LocalDateTime> {
  
    @Override
    public LocalDateTime convertTo(DateTime source, Type<LocalDateTime> destinationType) {
        DateTime dateTimeUtc = source.withZone(DateTimeZone.UTC);
        return LocalDateTime.of(dateTimeUtc.getYear(), dateTimeUtc.getMonthOfYear(), dateTimeUtc.getDayOfMonth(), dateTimeUtc.getHourOfDay(), dateTimeUtc.getMinuteOfHour());
    }

    @Override
    public DateTime convertFrom(LocalDateTime source, Type<DateTime> destinationType) {
        return new DateTime(DateTimeZone.UTC).withDate(
                source.getYear(), source.getMonthValue(), source.getDayOfMonth()
        ).withTime(0, 0, 0, 0);
    }
}
