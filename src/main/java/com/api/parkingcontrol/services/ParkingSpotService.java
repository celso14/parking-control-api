package com.api.parkingcontrol.services;

import com.api.parkingcontrol.entities.ParkingSpot;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParkingSpotService {

    @Autowired
    ParkingSpotRepository repository;

    public Page<ParkingSpot> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<ParkingSpot> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional //Garante o rollback -> alteração e deleção
    public ParkingSpot save(ParkingSpot parkingSpotEntity) {
        return this.repository.save(parkingSpotEntity);
    }

    @Transactional
    public void delete(ParkingSpot parkingSpot) {
        this.repository.delete(parkingSpot);
    }

    public boolean existsByParkingSpotNumber(String parkingSpotNumber) {
        return repository.existsByParkingSpotNumber(parkingSpotNumber);
    }

    public boolean existsByLicensePlateCar(String licensePlateCar) {
        return repository.existsByLicensePlateCar(licensePlateCar);
    }

    public boolean existsByApartmentAndBlock(String apartment, String block) {
        return repository.existsByApartmentAndBlock(apartment, block);
    }



}
