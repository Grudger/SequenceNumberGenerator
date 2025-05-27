package com.aamir.tracking.controller.dto.response;


import com.aamir.tracking.model.TrackingNumber;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Locale;

import java.util.UUID;

@Getter
@Setter
@Builder
public class TrackingNumberResponse {

    private String trackingId;
    private String originCountry;
    private String destinationCountry;
    private String weight;
    private UUID customerId;
    private String customerName;
    private String customerSlug;

    public static TrackingNumberResponse from(TrackingNumber trackingNumber) {
        return TrackingNumberResponse.builder()
                .trackingId(trackingNumber.getTrackingId())
                .originCountry(trackingNumber.getOriginCountryId().getCode())
                .destinationCountry(trackingNumber.getDestinationCountryId().getCode())
                .weight(String.format(Locale.UK, "%.3f", trackingNumber.getWeight() / 1000f))
                .customerId(trackingNumber.getCustomerId())
                .customerName(trackingNumber.getCustomerName())
                .customerSlug(trackingNumber.getCustomerSlug())
                .build();
    }

}
