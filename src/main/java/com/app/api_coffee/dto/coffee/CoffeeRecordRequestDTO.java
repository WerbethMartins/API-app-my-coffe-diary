package com.app.api_coffee.dto.coffee;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeRecordRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150, message = "Título deve ter no máximo 150 caracteres")
    private String title;

    @Size(max = 500, message = "Notas de sabor devem ter no máximo 500 caracteres")
    private String tastingNotes;

    @NotBlank(message = "O nome da loja é obrigatório")
    @Size(max = 120, message = "Nome da loja deve ter no máximo 120 caracteres")
    private String shopName;

    @NotNull(message = "As notas é obrigatória")
    private String notes;

    @Min(value = 1, message = "Avaliação minima é 1")
    @Max(value = 5, message = "Avaliação máxima é 5")
    private Integer rating;

    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal price;

    private String drinkType;

    @Size(max = 100)
    private String origin;

    // ID da loja (se o usuário escolher uma loja já cadastrada)
    private Long shopId;

    // Campo para upload de foto (por enquanto só a URL, o upload será tratado no Service)
    private String imageUrl;
}
