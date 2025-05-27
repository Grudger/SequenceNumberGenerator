package com.aamir.tracking.service.impl;

import com.aamir.tracking.controller.dto.request.TrackingNumberRequest;
import com.aamir.tracking.model.TrackingNumber;
import com.aamir.tracking.repository.TrackingNumberRepository;
import com.aamir.tracking.util.Country;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class TrackingNumberServiceImplTest {

    @Mock
    private TrackingNumberRepository trackingNumberRepository;

    @InjectMocks
    private TrackingNumberServiceImpl trackingNumberService;

    private TrackingNumberRequest sampleRequest;
    private String customerId;

    @BeforeEach
    void setUp() {
        // Set up the @Value fields using ReflectionTestUtils
        ReflectionTestUtils.setField(trackingNumberService, "pattern", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        ReflectionTestUtils.setField(trackingNumberService, "idLength", 10);
        ReflectionTestUtils.setField(trackingNumberService, "instanceId", "01");
        ReflectionTestUtils.setField(trackingNumberService, "startRange", 1000);
        ReflectionTestUtils.setField(trackingNumberService, "endRange", 9999);
        ReflectionTestUtils.setField(trackingNumberService, "padding", 4);

        // Initialize counter before tests
        trackingNumberService.init();

        // Create a sample request for testing
        customerId = UUID.randomUUID().toString();
        sampleRequest = new TrackingNumberRequest(
                "MY", // sourceCountry
                "SG", // destinationCountry
                "2.5", // weight in kg
                customerId, // customerId
                "Test Customer" // customerName
        );
    }

    @Test
    void init_ShouldSetCounterToStartRange() {
        // Given
        ReflectionTestUtils.setField(trackingNumberService, "startRange", 2000);

        // When
        trackingNumberService.init();

        // Then
        String trackingNumber = trackingNumberService.getNextTrackingNumber();
        assertTrue(trackingNumber.endsWith("2000"),
                "First tracking number should end with start range value");
    }

    @Test
    void getNextTrackingNumber_ShouldGenerateSequentialNumbers() {
        // Given - setup in beforeEach

        // When
        String firstNumber = trackingNumberService.getNextTrackingNumber();
        String secondNumber = trackingNumberService.getNextTrackingNumber();

        // Then
        assertNotEquals(firstNumber, secondNumber, "Sequential tracking numbers should be different");

        // Extract numeric parts
        String firstNumericPart = firstNumber.substring(firstNumber.length() - 4);
        String secondNumericPart = secondNumber.substring(secondNumber.length() - 4);

        int firstValue = Integer.parseInt(firstNumericPart);
        int secondValue = Integer.parseInt(secondNumericPart);

        assertEquals(1, secondValue - firstValue,
                "Sequential tracking numbers should increment by 1");
    }

    @Test
    void getNextTrackingNumber_ShouldThrowExceptionWhenExceedingRange() {
        // Given
        // because post construct doesn't execute, we need to set the counter manually

        ReflectionTestUtils.setField(trackingNumberService, "startRange", 1000);
        ReflectionTestUtils.setField(trackingNumberService, "endRange", 1002);
        trackingNumberService.init();

        // When - Generate first two numbers
        log.info("Current counter at {}", ReflectionTestUtils.getField(trackingNumberService, "counter"));
        trackingNumberService.getNextTrackingNumber();
        trackingNumberService.getNextTrackingNumber();

        // Then - Next call should throw exception
        assertThrows(IllegalStateException.class, () -> trackingNumberService.getNextTrackingNumber());
    }

    @Test
    void createNextTrackingNumber_ShouldCreateTrackingNumberWithCorrectData() {
        // Given
        when(trackingNumberRepository.save(any(TrackingNumber.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TrackingNumber result = trackingNumberService.createNextTrackingNumber(sampleRequest);

        // Then
        assertNotNull(result, "Created tracking number should not be null");
        assertEquals(Country.MY, result.getOriginCountryId(), "Origin country should match request");
        assertEquals(Country.SG, result.getDestinationCountryId(), "Destination country should match request");
        assertEquals(2500, result.getWeight(), "Weight should be converted to grams");
        assertEquals(UUID.fromString(customerId), result.getCustomerId(), "Customer ID should match request");
        assertEquals("Test Customer", result.getCustomerName(), "Customer name should match request");
        assertEquals("test-customer", result.getCustomerSlug(), "Customer slug should be created correctly");
        assertNotNull(result.getCreatedAt(), "Created timestamp should not be null");
        assertNull(result.getUpdatedAt(), "Updated timestamp should be null for new records");

        // Verify tracking ID format
        String trackingId = result.getTrackingId();
        assertTrue(trackingId.startsWith("MY"), "Tracking ID should start with origin country code");
        assertTrue(trackingId.contains("SG"), "Tracking ID should contain destination country code");

        verify(trackingNumberRepository).save(any(TrackingNumber.class));
    }

    @Test
    void getAllTrackingNumbers_ShouldReturnAllTrackingNumbersFromRepository() {
        // Given
        List<TrackingNumber> expectedTrackingNumbers = Arrays.asList(
                createSampleTrackingNumber("MY01SG1000", Country.MY, Country.SG),
                createSampleTrackingNumber("SG01MY1001", Country.SG, Country.MY)
        );

        when(trackingNumberRepository.findAll()).thenReturn(expectedTrackingNumbers);

        // When
        List<TrackingNumber> result = trackingNumberService.getAllTrackingNumbers();

        // Then
        assertEquals(2, result.size(), "Should return the correct number of tracking numbers");
        assertEquals(expectedTrackingNumbers, result, "Should return the tracking numbers from repository");

        verify(trackingNumberRepository).findAll();
    }

    @Test
    void generateSequentialId_ShouldTruncateLongPrefix() {
        // Given
        ReflectionTestUtils.setField(trackingNumberService, "instanceId", "ID");
        trackingNumberService.init();

        // Create a request with a very long combined country code
        TrackingNumberRequest longPrefixRequest = new TrackingNumberRequest(
                "MY",
                "SG",
                "1.0",
                customerId,
                "Test Customer"
        );

        when(trackingNumberRepository.save(any(TrackingNumber.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TrackingNumber result = trackingNumberService.createNextTrackingNumber(longPrefixRequest);

        // Then
        String trackingId = result.getTrackingId();
        assertTrue(trackingId.length() <= 12,
                "Tracking ID with long prefix should be truncated to fit within length limits");
    }

    @Test
    void getCustomerSlug_ShouldConvertCustomerNameToSlug() {
        // Given
        ReflectionTestUtils.setField(trackingNumberService, "instanceId", "01");
        trackingNumberService.init();

        // Test with various customer names
        TrackingNumberRequest request1 = new TrackingNumberRequest(
                "MY", "SG", "1.0", customerId, "Test Customer Name");
        TrackingNumberRequest request2 = new TrackingNumberRequest(
                "MY", "SG", "1.0", customerId, "Test   Multiple   Spaces");
        TrackingNumberRequest request3 = new TrackingNumberRequest(
                "MY", "SG", "1.0", customerId, "Special-Character_Name");

        when(trackingNumberRepository.save(any(TrackingNumber.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TrackingNumber result1 = trackingNumberService.createNextTrackingNumber(request1);
        TrackingNumber result2 = trackingNumberService.createNextTrackingNumber(request2);
        TrackingNumber result3 = trackingNumberService.createNextTrackingNumber(request3);

        // Then
        assertEquals("test-customer-name", result1.getCustomerSlug(),
                "Should convert spaces to hyphens and lowercase");
        assertEquals("test-multiple-spaces", result2.getCustomerSlug(),
                "Should collapse multiple spaces into single hyphens");
        assertEquals("special-character_name", result3.getCustomerSlug(),
                "Should preserve existing special characters that are allowed");
    }

    // Helper method to create sample tracking numbers for testing
    private TrackingNumber createSampleTrackingNumber(String trackingId, Country origin, Country destination) {
        return TrackingNumber.builder()
                .withTrackingId(trackingId)
                .withOriginCountryId(origin)
                .withDestinationCountryId(destination)
                .withWeight(1000)
                .withCreatedAt(new Timestamp(System.currentTimeMillis()))
                .withCustomerId(UUID.randomUUID())
                .withCustomerName("Test Customer")
                .withCustomerSlug("test-customer")
                .build();
    }
}