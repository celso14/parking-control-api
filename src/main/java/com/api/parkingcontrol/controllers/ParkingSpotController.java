package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.entities.ParkingSpot;
import com.api.parkingcontrol.services.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    @Autowired
    ParkingSpotService service;

    @GetMapping()
    public ResponseEntity<Page<ParkingSpot>> getAllParkingSpots(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id")UUID id){
        Optional<ParkingSpot> parkingSpotEntityOptional = service.findById(id);

        if(parkingSpotEntityOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotEntityOptional.get());
    }

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){

        if(service.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        }
        if(service.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        }
        if(service.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
        }

        var parkingSpotEntity = new ParkingSpot();

        //Conversão do DTO em Entity
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotEntity);

        //Set da Data de registro
        parkingSpotEntity.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(parkingSpotEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,
            @RequestBody @Valid ParkingSpotDto parkingSpotDto){

        Optional<ParkingSpot> parkingSpotEntityOptional = service.findById(id);

        if(parkingSpotEntityOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }

        var parkingSpotEntity = parkingSpotEntityOptional.get();

/*        parkingSpotEntity.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
        parkingSpotEntity.setLicensePlateCar(parkingSpotDto.getLicensePlateCar());
        parkingSpotEntity.setModelCar(parkingSpotDto.getModelCar());
        parkingSpotEntity.setBrandCar(parkingSpotDto.getBrandCar());
        parkingSpotEntity.setColorCar(parkingSpotDto.getColorCar());
        parkingSpotEntity.setOwner(parkingSpotDto.getOwner());
        parkingSpotEntity.setApartment(parkingSpotDto.getApartment());
        parkingSpotEntity.setBlock(parkingSpotDto.getBlock());*/

        BeanUtils.copyProperties(parkingSpotDto, parkingSpotEntity);
        parkingSpotEntity.setId(parkingSpotEntityOptional.get().getId());
        parkingSpotEntity.setRegistrationDate(parkingSpotEntityOptional.get().getRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.save(parkingSpotEntity));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id")UUID id){
        Optional<ParkingSpot> parkingSpotEntityOptional = service.findById(id);

        if(parkingSpotEntityOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }

        service.delete(parkingSpotEntityOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully.");
    }
}
