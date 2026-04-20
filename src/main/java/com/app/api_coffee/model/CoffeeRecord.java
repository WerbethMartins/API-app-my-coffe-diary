package com.app.api_coffee.model;

import com.app.api_coffee.enums.DrinkType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coffee_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "tasting_notes", length = 500)
    private String tastingNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Min(value = 1, message = "Avaliação minima é 1")
    @Max(value = 5, message = "Avaliação máxima é 5")
    @Column(nullable = false)
    private Integer rating;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "drink_type")
    private DrinkType drinkType;

    private String origin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @PrePersist
    protected void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }
}