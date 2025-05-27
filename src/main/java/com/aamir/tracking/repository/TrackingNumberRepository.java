package com.aamir.tracking.repository;

import com.aamir.tracking.model.TrackingNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrackingNumberRepository extends JpaRepository<TrackingNumber, UUID> {

}
