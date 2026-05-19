package com.app.api_coffee.repository;

import com.app.api_coffee.model.CoffeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoffeeRecordRepository extends JpaRepository<CoffeeRecord, Long> {

    // Busca registro de um usuário ordenados por data
    List<CoffeeRecord> findByUserIdOrderByRecordedAtDesc(Long userId);

    // Busca registro com nota maior ou igual a X (ex: nota >= 4)
    List<CoffeeRecord> findByNotesGreaterThanEqual(Long userId,Integer rating);

    // Contar quantos cafés um usuário já registrou
    long countByUserId(Long userId);
}
