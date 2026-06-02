package com.app.api_coffee.controller;

import com.app.api_coffee.dto.PageResponseDTO;
import com.app.api_coffee.dto.coffee.CoffeeRecordRequestDTO;
import com.app.api_coffee.dto.coffee.CoffeeRecordResponseDTO;
import com.app.api_coffee.security.JwtUtil;
import com.app.api_coffee.service.CoffeeRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coffee-records")
@RequiredArgsConstructor
public class CoffeeRecordController {

    private final CoffeeRecordService coffeeRecordService;
    private final JwtUtil jwtUtil;

    /*
    *   Criar um novo registro de café
    * */

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CoffeeRecordResponseDTO> createRecord(
            @RequestHeader("Authorization") String authHeader,
            @Valid @ModelAttribute CoffeeRecordRequestDTO requestDTO) {

        Long userId = extractUserIdFromHeader(authHeader);

        CoffeeRecordResponseDTO response = coffeeRecordService.createRecord(userId, requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*
    * Lista os cafés por página
    * */

    @GetMapping
    public ResponseEntity<PageResponseDTO<CoffeeRecordResponseDTO>> getMyRecordsByPage(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size  
){
        Long userId = extractUserIdFromHeader(authHeader);

        Pageable pageable = PageRequest.of(page, size, Sort.by("recordedAt").descending());

        PageResponseDTO<CoffeeRecordResponseDTO> responseDTO =
                coffeeRecordService.getRecordsByUserPaginated(userId, pageable);

        return ResponseEntity.ok(responseDTO);
    }

    /*
    * Buscar os registros específico por Id
    * */

    @GetMapping("/{id}")
    public ResponseEntity<CoffeeRecordResponseDTO> getRecordById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {

        Long userId = extractUserIdFromHeader(authHeader);

        CoffeeRecordResponseDTO record = coffeeRecordService.findById(userId);
        return ResponseEntity.ok(record);
    }

    /*
    * Deletar um registro
    * */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long id,
            @RequestHeader("user-id") Long userId
    ){
        coffeeRecordService.deleteRecord(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Metodo auxiliar para extrair userId do token
     */
    private Long extractUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token não encontrado ou inválido");
        }

        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

}
