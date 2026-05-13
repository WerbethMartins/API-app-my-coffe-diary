package com.app.api_coffee.controller;

import com.app.api_coffee.dto.coffee.CoffeeRecordRequestDTO;
import com.app.api_coffee.dto.coffee.CoffeeRecordResponseDTO;
import com.app.api_coffee.service.CoffeeRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coffee-records")
@RequiredArgsConstructor
public class CoffeeRecordController {

    private final CoffeeRecordService coffeeRecordService;

    /*
    *   Criar um novo registro de café
    * */

    @PostMapping
    public ResponseEntity<CoffeeRecordResponseDTO> createRecord(
            @RequestHeader("user-id") Long userId, // Temporário
            @Valid @RequestBody CoffeeRecordRequestDTO requestDTO
    ) {
        CoffeeRecordResponseDTO response = coffeeRecordService.createRecord(userId, requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*
    *  Listar todos os registrtos do usuário
    * */

    @GetMapping
    public  ResponseEntity<List<CoffeeRecordResponseDTO>> getMyRecords(
            @RequestHeader(value = "user-id", required = false) Long userId
    ) {
        if(userId == null) {
            userId = 1L; // Valor padrão para testes
        }
        List<CoffeeRecordResponseDTO> records = coffeeRecordService.listByUser(userId);
        return ResponseEntity.ok(records);
    }

    /*
    * Buscar os registros específico por Id
    * */

    @GetMapping("/{id}")
    public ResponseEntity<CoffeeRecordResponseDTO> getRecordById(
            @PathVariable Long id,
            @RequestHeader("user-id") Long userId
    ) {
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
}
