package com.example.uberlocationservice.services;

import com.example.uberlocationservice.dtos.NearByDriversResponseDTO;

import java.util.List;

public interface LocationService {

    Boolean saveDriverLocation(Double longitude, Double latitude, String driverId);

    List<NearByDriversResponseDTO> findNearByDrivers(Double longitude, Double latitude, Double radiusToSearch);

}
