package com.app.api_coffee.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponseDTO {

    private Long id;
    private String name;
    private String address;
    private String description;
    private String city;
    private String phone;
    private String imageUrl;

    // Estatísticas úteis
    private Integer totalCafesRegistrados;
    private Double notaMedia;
}
