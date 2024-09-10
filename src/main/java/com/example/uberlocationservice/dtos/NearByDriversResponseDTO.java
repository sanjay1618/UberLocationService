package com.example.uberlocationservice.dtos;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class NearByDriversResponseDTO {

    private String driverId;

    private Double longitude;

    private Double latitude;


}
