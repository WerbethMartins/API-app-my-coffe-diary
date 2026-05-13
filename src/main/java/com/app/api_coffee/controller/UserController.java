package com.app.api_coffee.controller;

import com.app.api_coffee.dto.user.LoginRequestDTO;
import com.app.api_coffee.dto.user.LoginResponseDTO;
import com.app.api_coffee.dto.user.UserRequestDTO;
import com.app.api_coffee.dto.user.UserResponseDTO;
import com.app.api_coffee.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO response = userService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        LoginResponseDTO responseDTO = userService.login(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@Valid @PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

}
