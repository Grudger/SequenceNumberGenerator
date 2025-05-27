package com.aamir.tracking.repository;

import com.aamir.tracking.model.TrackingNumber;
import com.aamir.tracking.util.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingNumberRepository extends JpaRepository<TrackingNumber, String> {
    
    @Query("SELECT t FROM TrackingNumber t WHERE " +
           "(:originCountry IS NULL OR t.originCountryId = :originCountry) AND " +
           "(:destinationCountry IS NULL OR t.destinationCountryId = :destinationCountry) AND " +
           "(:weight IS NULL OR t.weight = :weight) AND " +
           "(:createdAt IS NULL OR " +
           "FUNCTION('FORMATDATETIME', t.createdAt, 'yyyy-MM-dd') = FUNCTION('FORMATDATETIME', :createdAt, 'yyyy-MM-dd')) AND " +
           "(:customerId IS NULL OR t.customerId = :customerId) AND " +
           "(:customerName IS NULL OR LOWER(t.customerName) LIKE LOWER(CONCAT('%', :customerName, '%'))) AND " +
           "(:customerSlug IS NULL OR t.customerSlug = :customerSlug)")
    List<TrackingNumber> findByFilters(
            @Param("originCountry") Country originCountry,
            @Param("destinationCountry") Country destinationCountry,
            @Param("weight") Integer weight,
            @Param("createdAt") Timestamp createdAt,
            @Param("customerId") UUID customerId,
            @Param("customerName") String customerName,
            @Param("customerSlug") String customerSlug
    );
}