package com.app.api_coffee.dto.user;

import jakarta.validation.constraints.NotBlank;

public class loginRequestDTO {
    @NotBlank(message = "Username ou email é obrigatório!")
    private String usernameOrEmail;

    @NotBlank(message = "A senha é obrigatória!")
    private String password;
}
