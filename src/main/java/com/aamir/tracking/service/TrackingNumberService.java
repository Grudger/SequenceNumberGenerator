package com.aamir.tracking.service;


import com.aamir.tracking.controller.dto.request.TrackingNumberRequest;
import com.aamir.tracking.model.TrackingNumber;

import java.util.List;


public interface TrackingNumberService {

    String getNextTrackingNumber();

    List<TrackingNumber> getAllTrackingNumbers();

    TrackingNumber createNextTrackingNumber(TrackingNumberRequest request);

}
