package com.aamir.tracking.service.impl;

import com.aamir.tracking.controller.dto.request.TrackingNumberRequest;
import com.aamir.tracking.model.TrackingNumber;
import com.aamir.tracking.repository.TrackingNumberRepository;
import com.aamir.tracking.service.TrackingNumberService;
import com.aamir.tracking.util.Country;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class TrackingNumberServiceImpl implements TrackingNumberService {

    private final AtomicInteger counter = new AtomicInteger(0);
    private final TrackingNumberRepository trackingNumberRepository;
    // can be adjusted for characters
    @Value("${tracking.id.pattern}")
    private String pattern;
    // length adjustment this between 1-16
    @Value("${tracking.id.length}")
    private int idLength;
    // instance ID to prevent clashes across multiple instances
    @Value("${tracking.id.instance.id}")
    private String instanceId;
    @Value("${tracking.id.start.range}")
    private int startRange;
    @Value("${tracking.id.end.range}")
    private int endRange;
    @Value("${tracking.id.padding}")
    private int padding;

    public TrackingNumberServiceImpl(TrackingNumberRepository trackingNumberRepository) {
        this.trackingNumberRepository = trackingNumberRepository;

    }

    @PostConstruct
    public void init() {
        if (counter.get() < startRange) {
            counter.set(startRange);
            log.info("counter reset to start range: {}", startRange);
        }
    }

    @Override
    public String getNextTrackingNumber() {
        log.info("Generating next tracking number");
        return generateSequentialId(Country.MY.getCode(), instanceId, startRange, endRange);
    }

    @Override
    public TrackingNumber createNextTrackingNumber(TrackingNumberRequest request) {
        log.info("Creating next tracking number");
        float weight = Float.parseFloat(request.getWeight()) * 1000F;
        TrackingNumber trackingNumber = TrackingNumber.builder()
                .withTrackingId(generateSequentialId(
                        Country.valueOf(request.getSourceCountry()).getCode() +
                                Country.valueOf(request.getDestinationCountry()).getCode(), instanceId, startRange,
                        endRange))
                .withOriginCountryId(Country.valueOf(request.getSourceCountry()))
                .withDestinationCountryId(Country.valueOf(request.getDestinationCountry()))
                // Convert kg to grams
                .withWeight((int) weight)
                .withCreatedAt(Timestamp.from(Instant.now()))
                .withUpdatedAt(null)
                .withCustomerId(UUID.fromString(request.getCustomerId()))
                .withCustomerName(request.getCustomerName())
                .withCustomerSlug(getCustomerSlug(request.getCustomerName()))
                .build();

        return trackingNumberRepository.save(trackingNumber);
    }

    @Override
    public List<TrackingNumber> filterTrackingNumbers(Country originCountry, Country destinationCountry, String weight,
                                                     String createdAt, String customerId, String customerName,
                                                     String customerSlug) {
        log.info("Filtering tracking numbers with criteria - originCountry: {}, destinationCountry: {}, weight: {}," +
                        " createdAt: {}, customerId: {}, customerName: {}, customerSlug: {}",
                originCountry, destinationCountry, weight, createdAt, customerId, customerName, customerSlug);

        // Convert weight from kg to grams if provided
        Integer weightInGrams = null;
        if (weight != null && !weight.isEmpty()) {
            try {
                weightInGrams = (int) (Float.parseFloat(weight) * 1000F);
            } catch (NumberFormatException e) {
                log.warn("Invalid weight format: {}", weight);
                // Return empty list for invalid weight
                return List.of();
            }
        }

        // Parse created at timestamp if provided
        Timestamp createdAtTimestamp = null;
        if (createdAt != null && !createdAt.isEmpty()) {
            try {
                // Try to parse as yyyy-MM-dd format
                if (createdAt.length() == 10) { // Format: yyyy-MM-dd
                    createdAtTimestamp = Timestamp.valueOf(createdAt + " 00:00:00");
                } else {
                    // Try to parse as timestamp
                    createdAtTimestamp = Timestamp.valueOf(createdAt);
                }
            } catch (IllegalArgumentException e) {
                log.warn("Invalid createdAt format: {}. Expected format: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss", createdAt);
                // Return empty list for invalid date
                return List.of();
            }
        }

        // Parse customer ID to UUID if provided
        UUID customerUuid = null;
        if (customerId != null && !customerId.isEmpty()) {
            try {
                customerUuid = UUID.fromString(customerId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid UUID format for customerId: {}", customerId);
                // Return empty list for invalid UUID
                return List.of();
            }
        }

        // Execute the query with all parameters
        return trackingNumberRepository.findByFilters(
                originCountry,
                destinationCountry,
                weightInGrams,
                createdAtTimestamp,
                customerUuid,
                customerName,
                customerSlug
        );
    }

    @Override
    public List<TrackingNumber> getAllTrackingNumbers() {
        return trackingNumberRepository.findAll();
    }

    /**
     * Generates a tracking ID with a prefix and a sequential number within a specific range.
     * This implementation ensures uniqueness across multiple application instances by
     * allocating a specific range to each instance.
     *
     * @param prefix     The prefix to use (should be uppercase letters)
     * @param instanceId The ID of this application instance
     * @param startRange The starting number of the range for this instance
     * @param endRange   The ending number of the range for this instance
     * @return A string tracking ID
     */
    private String generateSequentialId(String prefix, String instanceId, int startRange, int endRange) {
        // Ensure we don't exceed 16 chars total for the final ID
        if (prefix.length() > 6) {
            prefix = prefix.substring(0, 6);
        }

        // Calculate the next sequence number within the instance's range
        // This ensures each instance has its own unique set of IDs
        int currentSequence =
                counter.getAndUpdate(current -> {
                    int next = current + 1;
                    // If we reach the end of our range, throw an exception instead of wrapping
                    if (next > endRange) {
                        throw new IllegalStateException("Tracking number sequence exhausted: reached end of range");
                    }
                    return Math.max(next, startRange);
                });


        // Use StringBuilder for better performance
        StringBuilder idBuilder = new StringBuilder(prefix.length() + instanceId.length() + padding);
        idBuilder.append(prefix);
        idBuilder.append(instanceId);

        // Calculate the number to append
        String numberStr = Integer.toString(currentSequence);

        // Pad with zeros if needed
        int zerosToPad = padding - numberStr.length();
        idBuilder.append("0".repeat(Math.max(0, zerosToPad)));

        // Append the actual number
        idBuilder.append(numberStr);

        return idBuilder.toString();
    }

    private String getCustomerSlug(String customerName) {
        return customerName.replaceAll("\\s+", "-").toLowerCase();
    }

}