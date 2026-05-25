package com.app.api_coffee.dto.user;

import com.app.api_coffee.dto.coffee.CoffeeTopDTO;
import com.app.api_coffee.dto.shop.FavoriteShopDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDTO {

    private Long userId;
    private String username;
    private String fullName;

    private Integer totalCoffees;
    private Double generalAverageScore; // Nota media geral
    private Integer coffeesThisMonth;

    private FavoriteShopDTO favoriteShop;
    private List<CoffeeTopDTO> top3coffees;

}

