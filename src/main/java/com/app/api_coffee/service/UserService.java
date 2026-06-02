package com.app.api_coffee.service;

import com.app.api_coffee.dto.coffee.CoffeeTopDTO;
import com.app.api_coffee.dto.shop.FavoriteShopDTO;
import com.app.api_coffee.dto.user.*;
import com.app.api_coffee.exception.ResourceNotFoundException;
import com.app.api_coffee.model.CoffeeRecord;
import com.app.api_coffee.model.Shop;
import com.app.api_coffee.model.User;
import com.app.api_coffee.repository.CoffeeRecordRepository;
import com.app.api_coffee.repository.UserRepository;
import com.app.api_coffee.security.JwtUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CoffeeRecordRepository coffeeRecordRepository;

    @Value("${app.upload.dir:uploads/coffee-images}")
    private String uploadDir;

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
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(Set.of("ROLE_USER"))
                .build();

        // Upload da foto de perfil
        if (request.getProfilePictureUrl() != null && !request.getProfilePictureUrl().isEmpty()) {
            String photoUrl = saveProfilePicture(request.getProfilePictureUrl());
            user.setProfilePictureUrl(photoUrl);
        }

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

    // ==================== DASHBOARD ====================

    public UserDashboardDTO getUserDashboard(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CoffeeRecord> allRecords = coffeeRecordRepository.findByUserIdOrderByRecordedAtDesc(userId);

        double notaMediaGeral = allRecords.stream()
                .mapToInt(CoffeeRecord::getRating) // // Extrai as notas
                .average()
                .orElse(0.0); // Calcula a média, ou retorna 0.0 se não houver registros

        // Cafés deste mês
        int coffeesThisMonth = (int) allRecords.stream()
                .filter(r -> r.getRecordedAt().getMonthValue() == LocalDateTime.now().getMonthValue()
                && r.getRecordedAt().getYear() == LocalDateTime.now().getYear())
                .count();

        // Loja favorita (a com mais registros)
        Shop favoriteShop = allRecords.stream()
                .filter(r -> r.getShop() != null) // Filtra apenas registros com loja associada
                .collect(Collectors.groupingBy(CoffeeRecord::getShop, Collectors.counting())) // Agrupa por loja e conta os registros
                .entrySet().stream() // Encontra a loja com mais registros
                .max(Map.Entry.comparingByValue()) // Compara pelo número de registros
                .map(Map.Entry::getKey) // Retorna a loja favorita
                .orElse(null);

        // Top 3 cafês
        List<CoffeeTopDTO> top3 = allRecords.stream()
                .sorted((a, b) -> b.getRating().compareTo(a.getRating())) // Ordena por nota (descendente)
                .limit(3)
                .map(this::convertToCoffeeTopDTO)
                .collect(Collectors.toList());

        return UserDashboardDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .totalCoffees(allRecords.size())
                .generalAverageScore(Math.round(notaMediaGeral * 10.0) / 10.0)
                .coffeesThisMonth(coffeesThisMonth)
                .favoriteShop(favoriteShop != null ? convertToFavoriteShopDTO(favoriteShop, allRecords) : null)
                .top3coffees(top3)
                .build();
    }

    private FavoriteShopDTO convertToFavoriteShopDTO(Shop shop, List<CoffeeRecord> allRecords) {
        long visits = allRecords.stream()
                .filter(r -> r.getShop() != null && r.getShop().getId().equals(shop.getId()))
                .count();

        double media = allRecords.stream()
                .filter(r -> r.getShop() != null && r.getShop().getId().equals(shop.getId()))
                .mapToInt(CoffeeRecord::getRating)
                .average()
                .orElse(0.0);

        return FavoriteShopDTO.builder()
                .id(shop.getId())
                .name(shop.getName())
                .AverageScore(Math.round(media * 10.0) / 10.0)
                .totalVisits((int) visits)
                .build();
    }

    private CoffeeTopDTO convertToCoffeeTopDTO(CoffeeRecord record) {
        return CoffeeTopDTO.builder()
                .id(record.getId())
                .title(record.getTitle())
                .rating(record.getRating())
                .shopName(record.getShop() != null ? record.getShop().getName() : null)
                .build();
    }

    // Metodo auxiliar para salvar foto de perfil
    private String saveProfilePicture(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);

            file.transferTo(filePath.toFile());

            return "/uploads/coffee-images/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar foto de perfil: " + e.getMessage());
        }
    }

}

