package com.aamir.tracking.controller;


import com.aamir.tracking.controller.dto.request.TrackingNumberRequest;
import com.aamir.tracking.controller.dto.response.TrackingNumberResponse;
import com.aamir.tracking.service.TrackingNumberService;
import com.aamir.tracking.util.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController(value = "/v1")
@RequiredArgsConstructor
@Slf4j
public class TrackingNumberController {

    private final TrackingNumberService trackingNumberService;

    @GetMapping(path = "/getAll", produces = "application/json")
    public ResponseEntity<List<TrackingNumberResponse>> get() {
        log.info("Getting all tracking numbers");
        return ResponseEntity.ok(
                trackingNumberService.getAllTrackingNumbers().stream().map(TrackingNumberResponse::from).toList());
    }

    @GetMapping(path = "/next-tracking-number", produces = "application/json")
    public ResponseEntity<String> getNextId() {
        log.info("Getting next tracking number");
        return ResponseEntity.ok(trackingNumberService.getNextTrackingNumber());
    }

    @PostMapping(path = "/create", consumes = "application/json")
    public ResponseEntity<TrackingNumberResponse> createNextId(@RequestBody TrackingNumberRequest trackingNumber) {
        log.info("Creating next tracking number");
        return ResponseEntity.ok(
                TrackingNumberResponse.from(trackingNumberService.createNextTrackingNumber(trackingNumber))
        );
    }

    @GetMapping(path = "/filter", produces = "application/json")
    public ResponseEntity<List<TrackingNumberResponse>> filter(
            @RequestParam(value = "origin_country_id", required = false) Country originCountry,
            @RequestParam(value = "destination_country_id", required = false) Country destinationCountry,
            @RequestParam(value = "weight", required = false) String weight,
            @RequestParam(value = "created_at", required = false) String createdAt,
            @RequestParam(value = "customer_id", required = false) String customerId,
            @RequestParam(value = "customer_name", required = false) String customerName,
            @RequestParam(value = "customer_slug", required = false) String customerSlug
    ) {
        log.info("Filtering tracking numbers");
        return ResponseEntity.ok(
                trackingNumberService.filterTrackingNumbers(originCountry, destinationCountry, weight, createdAt,
                        customerId, customerName, customerSlug).stream().map(TrackingNumberResponse::from).toList());
    }

}
