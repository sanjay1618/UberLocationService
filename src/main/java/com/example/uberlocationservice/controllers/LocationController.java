package com.example.uberlocationservice.controllers;


import com.example.uberlocationservice.dtos.NearByDriversRequestDTO;
import com.example.uberlocationservice.dtos.SaveDriverLocationRequestDTO;
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

    @Autowired
    public LocationController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDTO saveDriverLocationRequestDTO){
        try{
            GeoOperations<String, String> geoOPs = stringRedisTemplate.opsForGeo();
            Long location = geoOPs.add(DRIVER_OPS_KEY,
                    new RedisGeoCommands.GeoLocation<String>(saveDriverLocationRequestDTO.getDriverId(),
                            new Point(saveDriverLocationRequestDTO.getLongitude(), saveDriverLocationRequestDTO.getLatitude())));
          System.out.println(location);
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(false,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nearbyDrivers")
    public ResponseEntity<List<String>> getNearByDrivers(@RequestBody NearByDriversRequestDTO nearByDriversRequestDTO) {
      try {
          GeoOperations<String, String> geoOPs = stringRedisTemplate.opsForGeo();
          Distance distanceWithMetrics = new Distance(RADIUS_TO_SEARCH, Metrics.KILOMETERS);
          Circle withIn = new Circle(new Point(nearByDriversRequestDTO.getLongitude(), nearByDriversRequestDTO.getLatitude()), distanceWithMetrics);
          GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOPs.radius(DRIVER_OPS_KEY, withIn, RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates().includeDistance()
          );

          List<String> nearByDrivers = new ArrayList<>();
          for (GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult : results) {
              nearByDrivers.add(geoResult.getContent().toString());
              System.out.println(geoResult.toString());
          }

          return new ResponseEntity<List<String>>(nearByDrivers, HttpStatus.OK);

      } catch (Exception e) {
          return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
      }

    }



}
