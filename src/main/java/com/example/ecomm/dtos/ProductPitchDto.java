package com.example.ecomm.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class ProductPitchDto {
    private UUID productId;
    private String notification;
}
