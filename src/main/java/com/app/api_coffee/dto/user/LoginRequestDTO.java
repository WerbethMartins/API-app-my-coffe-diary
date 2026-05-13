package com.app.api_coffee.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Username ou email é obrigatório!")
    private String usernameOrEmail;

    @NotBlank(message = "A senha é obrigatória!")
    private String password;
}
