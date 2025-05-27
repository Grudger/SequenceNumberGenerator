package com.aamir.tracking.model;

import com.aamir.tracking.util.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "tracking_number", schema = "public")
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class TrackingNumber {

    @Id
    @Column(name = "tracking_id", unique = true)
    private String trackingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin_country_id")
    private Country originCountryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_country_id")
    private Country destinationCountryId;

    // weight in grams
    @Column(name = "weight")
    private Integer weight;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_slug")
    private String customerSlug;
}