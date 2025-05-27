package com.aamir.tracking.config;

import com.aamir.tracking.model.TrackingNumber;
import com.aamir.tracking.repository.TrackingNumberRepository;
import com.aamir.tracking.util.Country;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    private final TrackingNumberRepository trackingNumberRepository;

    @PostConstruct
    @Transactional
    void init() {
        // Check if data already exists
        if (trackingNumberRepository.count() > 0) {
            // Skip initialization if data already exists
            return;
        }

        List<TrackingNumber> trackingNumbers = new ArrayList<>();

        // Sample tracking number 1
        TrackingNumber tn1 = new TrackingNumber();
        tn1.setOriginCountryId(Country.US);
        tn1.setDestinationCountryId(Country.BR);
        tn1.setWeight(500);
        tn1.setCreatedAt(Timestamp.from(Instant.now()));
        tn1.setUpdatedAt(Timestamp.from(Instant.now()));
        tn1.setCustomerId(UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-1e2f3a4b5c6d"));
        tn1.setCustomerName("John Doe");
        tn1.setCustomerSlug("john-doe");
        tn1.setTrackingId("1Z9RB0Y89E");
        trackingNumbers.add(tn1);

        // Sample tracking number 2
        TrackingNumber tn2 = new TrackingNumber();
        tn2.setOriginCountryId(Country.CN);
        tn2.setDestinationCountryId(Country.TH);
        tn2.setWeight(750);
        tn2.setCreatedAt(Timestamp.from(Instant.now()));
        tn2.setUpdatedAt(Timestamp.from(Instant.now()));
        tn2.setCustomerId(UUID.fromString("b2c3d4e5-f6a7-5b6c-9d0e-2f3a4b5c6d7e"));
        tn2.setCustomerName("Jane Smith");
        tn2.setCustomerSlug("jane-smith");
        tn2.setTrackingId("2Z9RB0Y89F");
        trackingNumbers.add(tn2);

        // Sample tracking number 3
        TrackingNumber tn3 = new TrackingNumber();
        tn3.setOriginCountryId(Country.CN);
        tn3.setDestinationCountryId(Country.MY);
        tn3.setWeight(1200);
        tn3.setCreatedAt(Timestamp.from(Instant.now()));
        tn3.setUpdatedAt(Timestamp.from(Instant.now()));
        tn3.setCustomerId(UUID.fromString("c3d4e5f6-a7b8-6c7d-0e1f-3a4b5c6d7e8f"));
        tn3.setCustomerName("Akira Tanaka");
        tn3.setCustomerSlug("akira-tanaka");
        tn3.setTrackingId("3Z9RB0Y89G");
        trackingNumbers.add(tn3);

        // Sample tracking number 4
        TrackingNumber tn4 = new TrackingNumber();
        tn4.setOriginCountryId(Country.VN);
        tn4.setDestinationCountryId(Country.MY);
        tn4.setWeight(850);
        tn4.setCreatedAt(Timestamp.from(Instant.now()));
        tn4.setUpdatedAt(Timestamp.from(Instant.now()));
        tn4.setCustomerId(UUID.fromString("f1c4d4e6-f6a9-5b6c-9d2e-3f3a4b5c6d7e"));
        tn4.setCustomerName("David Johnson");
        tn4.setCustomerSlug("david-johnson");
        tn4.setTrackingId("4Z9RB0Y89H");
        trackingNumbers.add(tn4);

        // Save all tracking numbers
        trackingNumberRepository.saveAll(trackingNumbers);

        log.info("Database initialized with {} tracking number records", trackingNumbers.size());
    }
}