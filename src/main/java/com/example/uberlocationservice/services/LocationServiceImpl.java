package com.example.uberlocationservice.services;

import com.example.uberlocationservice.dtos.NearByDriversResponseDTO;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class LocationServiceImpl implements LocationService {

    private static final String DRIVER_OPS_KEY="drivers";

    private final StringRedisTemplate stringRedisTemplate;

    public LocationServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Boolean saveDriverLocation(Double longitude, Double latitude, String driverId) {
        try {
            GeoOperations<String, String> geoOPs = stringRedisTemplate.opsForGeo();
            geoOPs.add(DRIVER_OPS_KEY, new RedisGeoCommands.GeoLocation<>(driverId, new Point(longitude, latitude)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<NearByDriversResponseDTO> findNearByDrivers(Double longitude, Double latitude, Double radiusToSearch) {
      try {
          GeoOperations<String, String> geoOPs = stringRedisTemplate.opsForGeo();
          Distance distanceToSearchWithMetrics = new Distance(radiusToSearch, Metrics.KILOMETERS);
          Circle withInCircle = new Circle(new Point(longitude, latitude), distanceToSearchWithMetrics);
          GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOPs.radius(DRIVER_OPS_KEY, withInCircle,RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates().includeDistance());

         //Creating the response DTO from the ResultSet
          List<NearByDriversResponseDTO> nearByDrivers = new ArrayList<>();
          for (GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult : results) {
              NearByDriversResponseDTO responseDTO = new NearByDriversResponseDTO();
              responseDTO.setDriverId(geoResult.getContent().getName());
              responseDTO.setLongitude(geoResult.getContent().getPoint().getX());
              responseDTO.setLatitude(geoResult.getContent().getPoint().getY());
              nearByDrivers.add(responseDTO);
          }

          return nearByDrivers;
      } catch (Exception e) {
          e.printStackTrace();
          return null;
      }
    }
}
