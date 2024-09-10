package com.example.uberlocationservice.controllers;


import com.example.uberlocationservice.dtos.NearByDriversRequestDTO;
import com.example.uberlocationservice.dtos.NearByDriversResponseDTO;
import com.example.uberlocationservice.dtos.SaveDriverLocationRequestDTO;
import com.example.uberlocationservice.services.LocationService;
import com.example.uberlocationservice.services.LocationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.repository.Near;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private static final String DRIVER_OPS_KEY="drivers";

    private static final Double RADIUS_TO_SEARCH= 5.0;

    private final StringRedisTemplate stringRedisTemplate;
    private final LocationServiceImpl locationService;

    @Autowired
    public LocationController(StringRedisTemplate stringRedisTemplate, LocationServiceImpl locationService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.locationService = locationService;
    }


    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDTO saveDriverLocationRequestDTO){
        Boolean isDriverLocationSaved =  locationService.saveDriverLocation(saveDriverLocationRequestDTO.getLongitude(), saveDriverLocationRequestDTO.getLatitude(), saveDriverLocationRequestDTO.getDriverId());
        if (isDriverLocationSaved){
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/nearbyDrivers")
    public ResponseEntity<List<NearByDriversResponseDTO>> getNearByDrivers(@RequestBody NearByDriversRequestDTO nearByDriversRequestDTO) {
        List<NearByDriversResponseDTO> nearByDriversResponseDTOList = locationService.findNearByDrivers(nearByDriversRequestDTO.getLongitude(), nearByDriversRequestDTO.getLatitude(), 5.0);
        if (nearByDriversResponseDTOList.isEmpty()){
            return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(nearByDriversResponseDTOList, HttpStatus.OK);



    }



}
