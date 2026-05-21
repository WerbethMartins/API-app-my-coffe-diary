package com.app.api_coffee.repository;

import com.app.api_coffee.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    // Busca a loja por nome
    Optional<Shop> findByNameIgnoreCase(String name);

    // Busca lojas por cidade
    List<Shop> findByCityIgnoreCase(String city);

    // Busca loja que contenham um nome(busca parcial)
    List<Shop> findByNameContainingIgnoreCase(String name);

    @Query("SELECT AVG(cr.rating) FROM CoffeeRecord cr WHERE cr.shop.id = :shopId")
    Double findAverageRatingByShopId(@Param("shopId") Long shopId);
}
