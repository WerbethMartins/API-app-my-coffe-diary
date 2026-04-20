package com.app.api_coffee.dto.user;

import jakarta.validation.constraints.Email;
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
public class UserRequestDTO {
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email é invalido!")
    private String email;

    @NotBlank(message = "Senha é obrigatório")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String password;

    @Size(max = 150, message = "Nome completo dever ter no máximo 150 caracteres")
    private String fullName;
}
