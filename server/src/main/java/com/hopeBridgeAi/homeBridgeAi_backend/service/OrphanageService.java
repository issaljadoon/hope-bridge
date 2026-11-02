package com.hopeBridgeAi.homeBridgeAi_backend.service;

import com.hopeBridgeAi.homeBridgeAi_backend.dto.OrphanageCreateRequest;
import com.hopeBridgeAi.homeBridgeAi_backend.dto.OrphanageUpdateRequest;
import com.hopeBridgeAi.homeBridgeAi_backend.dto.OrphanageResponse;
import com.hopeBridgeAi.homeBridgeAi_backend.entity.Orphanage;
import com.hopeBridgeAi.homeBridgeAi_backend.repository.OrphanageRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrphanageService {

    private final OrphanageRepository orphanageRepository;

    public OrphanageService(OrphanageRepository orphanageRepository) {
        this.orphanageRepository = orphanageRepository;
    }

    // 1. CREATE ORPHANAGE
    @Transactional
    public OrphanageResponse createOrphanage(OrphanageCreateRequest request) {

        validateCapacity(request.capacity(), request.currentChildren());

        Orphanage orphanage = new Orphanage();

        orphanage.setName(request.name().trim());
        orphanage.setAddress(request.address().trim());
        orphanage.setCity(request.city().trim());
        orphanage.setContactNumber(request.contactNumber());
        orphanage.setEmail(request.email());
        orphanage.setDescription(request.description());
        orphanage.setCapacity(request.capacity());
        orphanage.setCurrentChildren(request.currentChildren());
        orphanage.setActive(true);

        Orphanage savedOrphanage = orphanageRepository.save(orphanage);

        return mapToResponse(savedOrphanage);
    }

    // 2. GET ORPHANAGE BY ID
    @Transactional(readOnly = true)
    public OrphanageResponse getOrphanageById(Long id) {

        Orphanage orphanage = orphanageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Orphanage not found with ID: " + id
                ));

        if (!Boolean.TRUE.equals(orphanage.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Orphanage not found with ID: " + id
            );
        }

        return mapToResponse(orphanage);
    }

    // 3. GET ALL ACTIVE ORPHANAGES
    @Transactional(readOnly = true)
    public List<OrphanageResponse> getAllOrphanages() {

        return orphanageRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 4. UPDATE ORPHANAGE
    @Transactional
    public OrphanageResponse updateOrphanage(
            Long id,
            OrphanageUpdateRequest request) {

        Orphanage orphanage = orphanageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Orphanage not found with ID: " + id
                ));

        if (!Boolean.TRUE.equals(orphanage.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Orphanage not found with ID: " + id
            );
        }

        validateCapacity(request.capacity(), request.currentChildren());

        orphanage.setName(request.name().trim());
        orphanage.setAddress(request.address().trim());
        orphanage.setCity(request.city().trim());
        orphanage.setContactNumber(request.contactNumber());
        orphanage.setEmail(request.email());
        orphanage.setDescription(request.description());
        orphanage.setCapacity(request.capacity());
        orphanage.setCurrentChildren(request.currentChildren());

        Orphanage updatedOrphanage = orphanageRepository.save(orphanage);

        return mapToResponse(updatedOrphanage);
    }

    // 5. SOFT DELETE ORPHANAGE
    @Transactional
    public void deleteOrphanage(Long id) {

        Orphanage orphanage = orphanageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Orphanage not found with ID: " + id
                ));

        if (!Boolean.TRUE.equals(orphanage.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Orphanage not found with ID: " + id
            );
        }

        orphanage.setActive(false);

        orphanageRepository.save(orphanage);
    }

    // 6. VALIDATE CAPACITY
    private void validateCapacity(Integer capacity, Integer currentChildren) {

        if (capacity == null || capacity < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Capacity must be at least 1"
            );
        }

        if (currentChildren == null || currentChildren < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Current children cannot be negative"
            );
        }

        if (currentChildren > capacity) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Current children cannot exceed orphanage capacity"
            );
        }
    }

    // 7. ENTITY TO RESPONSE DTO
    private OrphanageResponse mapToResponse(Orphanage orphanage) {

        return new OrphanageResponse(
                orphanage.getId(),
                orphanage.getName(),
                orphanage.getAddress(),
                orphanage.getCity(),
                orphanage.getContactNumber(),
                orphanage.getEmail(),
                orphanage.getDescription(),
                orphanage.getCapacity(),
                orphanage.getCurrentChildren(),
                orphanage.getActive(),
                orphanage.getCreatedAt(),
                orphanage.getUpdatedAt()
        );
    }
}