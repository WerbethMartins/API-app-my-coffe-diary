package com.app.api_coffee.controller;

import com.app.api_coffee.dto.PageResponseDTO;
import com.app.api_coffee.dto.shop.ShopRequestDTO;
import com.app.api_coffee.dto.shop.ShopResponseDTO;
import com.app.api_coffee.security.JwtUtil;
import com.app.api_coffee.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;
    private final JwtUtil jwtUtil;

    /*
    * Listar cafeterias com paginação
    * */

    @GetMapping
    public ResponseEntity<PageResponseDTO<ShopResponseDTO>> getAllShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        PageResponseDTO<ShopResponseDTO> response = shopService.getShopsPaginated(pageable);
        return ResponseEntity.ok(response);
    }

    /*
    * Cria uma nova loja
    * */

    @PostMapping
    public ResponseEntity<ShopResponseDTO> createShop(@Valid @RequestBody ShopRequestDTO requestDTO) {

        ShopResponseDTO response = shopService.createShop(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<ShopResponseDTO> getShopById(@PathVariable Long id){
        ShopResponseDTO shop = shopService.getShopById(id);
        return ResponseEntity.ok(shop);
    }

    /*
    * Buscar loja por cidade
    * */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<ShopResponseDTO>> getShopsByCity(@PathVariable String city){
        List<ShopResponseDTO> shops = shopService.getShopsByCity(city);
        return ResponseEntity.ok(shops);
    }

    /*
    * Busca por nome (parcial)
    * */
    @GetMapping("/search")
    public ResponseEntity<List<ShopResponseDTO>> searchShops(@RequestParam String name){
        List<ShopResponseDTO> shops = shopService.searchShopsByName(name);
        return ResponseEntity.ok(shops);
    }

}
