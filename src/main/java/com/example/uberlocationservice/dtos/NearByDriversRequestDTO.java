package com.example.uberlocationservice.dtos;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearByDriversRequestDTO {

    private Double latitude;

    private Double longitude;
}
