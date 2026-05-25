package com.app.api_coffee.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteShopDTO {
    private Long id;
    private String name;
    private Double AverageScore;
    private Integer totalVisits;

}
