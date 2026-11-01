package com.hopeBridgeAi.homeBridgeAi_backend.repository;

import com.hopeBridgeAi.homeBridgeAi_backend.entity.Orphanage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrphanageRepository extends JpaRepository<Orphanage, Long> {

    // Find all active orphanages
    List<Orphanage> findByActiveTrue();

    // Find active orphanages by city
    List<Orphanage> findByCityIgnoreCaseAndActiveTrue(String city);

    // Check whether an active orphanage exists with this name
    boolean existsByNameIgnoreCaseAndActiveTrue(String name);
}