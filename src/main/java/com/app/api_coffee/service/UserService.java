package com.app.api_coffee.service;

import com.app.api_coffee.dto.user.LoginRequestDTO;
import com.app.api_coffee.dto.user.LoginResponseDTO;
import com.app.api_coffee.dto.user.UserRequestDTO;
import com.app.api_coffee.dto.user.UserResponseDTO;
import com.app.api_coffee.exception.ResourceNotFoundException;
import com.app.api_coffee.model.User;
import com.app.api_coffee.repository.CoffeeRecordRepository;
import com.app.api_coffee.repository.UserRepository;
import com.app.api_coffee.security.JwtUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO register(@Valid UserRequestDTO request){
        if(userRepository.existsByUsername(request.getUsername())) {
            throw  new RuntimeException("Username already exists");
        }

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash da senha
                .fullName(request.getFullName())
                .roles(Set.of("ROLE_USER")) // Role Padrão
                .build();

        User savedUser = userRepository.save(user);

        return convertToResponseDTO(savedUser);
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest){

        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(loginRequest.getUsernameOrEmail()));

        User user = userOptional.orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid username/email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // Token JWT
        return LoginResponseDTO.builder()
                .userId(user.getId())
                .username(user.getFullName())
                .email(user.getEmail())
                .token(token)
                .build();

    }

    public UserResponseDTO getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + id));
        return convertToResponseDTO(user);
    }

    private UserResponseDTO convertToResponseDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }

}

