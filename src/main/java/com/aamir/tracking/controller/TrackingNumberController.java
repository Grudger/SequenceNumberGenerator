package com.aamir.tracking.controller;


import com.aamir.tracking.controller.dto.request.TrackingNumberRequest;
import com.aamir.tracking.controller.dto.response.TrackingNumberResponse;
import com.aamir.tracking.service.TrackingNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

}
