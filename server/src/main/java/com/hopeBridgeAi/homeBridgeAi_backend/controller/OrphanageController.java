package com.hopeBridgeAi.homeBridgeAi_backend.controller;

import com.hopeBridgeAi.homeBridgeAi_backend.dto.OrphanageCreateRequest;
import com.hopeBridgeAi.homeBridgeAi_backend.dto.OrphanageResponse;
import com.hopeBridgeAi.homeBridgeAi_backend.dto.OrphanageUpdateRequest;
import com.hopeBridgeAi.homeBridgeAi_backend.service.OrphanageService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orphanages")
public class OrphanageController {

    private final OrphanageService orphanageService;

    public OrphanageController(OrphanageService orphanageService) {
        this.orphanageService = orphanageService;
    }

    // CREATE ORPHANAGE
    @PostMapping
    public ResponseEntity<OrphanageResponse> createOrphanage(
            @Valid @RequestBody OrphanageCreateRequest request) {

        OrphanageResponse response =
                orphanageService.createOrphanage(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET ALL ACTIVE ORPHANAGES
    @GetMapping
    public ResponseEntity<List<OrphanageResponse>> getAllOrphanages() {

        return ResponseEntity.ok(orphanageService.getAllOrphanages());
    }

    // GET ORPHANAGE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrphanageResponse> getOrphanageById(
            @PathVariable Long id) {

        return ResponseEntity.ok(orphanageService.getOrphanageById(id));
    }

    // UPDATE ORPHANAGE
    @PutMapping("/{id}")
    public ResponseEntity<OrphanageResponse> updateOrphanage(
            @PathVariable Long id,
            @Valid @RequestBody OrphanageUpdateRequest request) {

        return ResponseEntity.ok(
                orphanageService.updateOrphanage(id, request)
        );
    }

    // SOFT DELETE ORPHANAGE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrphanage(@PathVariable Long id) {

        orphanageService.deleteOrphanage(id);

        return ResponseEntity.noContent().build();
    }
}