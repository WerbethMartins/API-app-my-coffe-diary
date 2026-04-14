package com.app.api_coffe.repository;

import com.app.api_coffe.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    // Busca a loja por nome
    Optional<Shop> findByNameIgnoreCase(String name);

    // Busca lojas por cidade
    Optional<Shop> findByCityIgnoreCase(String city);

    // Busca loja que contenham um nome(busca parcial)
    Optional<Shop> findByNameContainingIgnoreCase(String name);
}
