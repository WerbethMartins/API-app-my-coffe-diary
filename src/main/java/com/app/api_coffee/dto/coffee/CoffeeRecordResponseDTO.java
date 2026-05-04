package com.app.api_coffee.dto.coffee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeRecordResponseDTO {
    private Long id;
    private String title;
    private String tastingNotes;
    private String nameShop;
    private Integer notes;
    private BigDecimal price;
    private String drinkType;
    private String origin;
    private String imageUrl;
    private LocalDateTime recordedAt;

    // Informações do usuário
    private Long userId;
    private String username;

    // Informações da loja
    private Long shopId;
    private String shopAddress;
}
