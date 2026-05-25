package com.app.api_coffee.dto.coffee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeTopDTO {
    private Long id;
    private String title;
    private Integer rating;
    private String shopName;

}
