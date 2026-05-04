package com.app.api_coffee.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shops") // lojas -> shops
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Shop name is required")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String city;

    @Column(length = 20)
    private String phone;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // Uma loja pode ter vários cafés
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CoffeeRecord> coffeeRecords = new HashSet<>();

    // Métodos auxiliares
    public void addCoffeeRecord(CoffeeRecord coffeeRecord) {
        this.coffeeRecords.add(coffeeRecord);
        coffeeRecord.setShop(this);
    }

    public void removeCoffeeRecord(CoffeeRecord coffeeRecord) {
        this.coffeeRecords.remove(coffeeRecord);
        coffeeRecord.setShop(null);
    }
}
