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
    List<CoffeeRecord> findByUserIdOrderDataRecordDesc(Long userId);

    // Busca por loja
    List<CoffeeRecord> findByShop(Long lojaId);

    // Busca registro com nota maior ou igual a X (ex: nota >= 4)
    List<CoffeeRecord> findByNoteGreaterThanEqual(Integer notes);

    // Busca personalizada: cafés de um usuário com mínima
    @Query("SELECT c FROM CafeRegistro c WHERE c.usuario.id = :usuarioId AND c.nota >= :notaMinima")
    List<CoffeeRecord> findByUsuarioIdAndNotaMinima(@Param("usuarioId") Long usuarioId,
                                                    @Param("notaMinima") Integer notaMinima);

    // Contar quantos cafés um usuário já registrou
    long countByuserId(Long userId);
}
