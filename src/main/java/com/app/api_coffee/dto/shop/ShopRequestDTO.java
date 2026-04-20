package com.app.api_coffee.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequestDTO {

    @NotBlank(message = "O nome da loja é obrigatório")
    @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
    private String name;

    @NotBlank(message = "O endereço é obrigatório")
    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String address;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String city;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String phone;

    // URL da foto da fachada da loja (opcional)
    private String imageUrl;
}
