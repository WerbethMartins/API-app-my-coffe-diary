package com.app.api_coffee.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private String token;
    private String type = "Bearer";
}
