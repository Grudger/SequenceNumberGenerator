package com.aamir.tracking.service;


import com.aamir.tracking.controller.dto.request.TrackingNumberRequest;
import com.aamir.tracking.model.TrackingNumber;
import com.aamir.tracking.util.Country;

import java.util.Arrays;
import java.util.List;


public interface TrackingNumberService {

    String getNextTrackingNumber();

    List<TrackingNumber> getAllTrackingNumbers();

    TrackingNumber createNextTrackingNumber(TrackingNumberRequest request);

    List<TrackingNumber> filterTrackingNumbers(Country originCountry, Country destinationCountry, String weight,
                                               String createdAt, String customerId, String customerName,
                                               String customerSlug);
}
