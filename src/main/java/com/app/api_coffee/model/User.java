package com.app.api_coffee.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "O formato do email é invalido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter nom mínimo 8 caracteres")
    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relacionamento com CoffeRecord
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CoffeeRecord> coffeeRecords = new HashSet<>();

    // Métodos auxiliares para manter a consistência bidirecional
    public void addCoffeeRecord(CoffeeRecord coffeeRecord) {
        this.coffeeRecords.add(coffeeRecord);
        coffeeRecord.setUser(this);
    }

    public void removeCoffeeRecord(CoffeeRecord coffeeRecord) {
        this.coffeeRecords.remove(coffeeRecord);
        coffeeRecord.setUser(null);
    }
}