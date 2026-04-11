package com.app.api_coffe.model;

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
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Column(nullable = false)
    private String password;

    @Column(length = 150)
    private String nomeCompleto;

    @Column(length = 500)
    private String fotoPerfilUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    // Relacionamento com os registros de café (boa prática bidirecional)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CafeRegistro> cafeRegistros = new HashSet<>();

    // Métodos Auxiliares para manter a consistência da relação Bidirecional
    // Em uma relação @OneToMany e @ManyToOne, o JPA/Hibernate precisa que os dois lados da relação estejam sincronizados
    public void addCafeRegistro(CafeRegistro cafeRegistro){
        this.cafeRegistros.add(cafeRegistro);
        cafeRegistro.setUsuario(this);
    }

    public void removerCafeRegistro(CafeRegistro cafeRegistro){
        this.cafeRegistros.remove(cafeRegistro);
        cafeRegistro.setUsuario(null);
    }
}
