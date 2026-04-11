package com.app.api_coffe.model;

import com.app.api_coffe.enums.TipoBebida;
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
@Table(name = "cafe_registros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CafeRegistro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 500)
    private String saborNotas;

    @Column(nullable = false)
    private String lojaNome;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    @Column(nullable = false)
    private Integer nota; // 1 a 5

    @Positive(message = "O preço deve se positivo")
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    private TipoBebida tipoBebida;

    private String origem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    @Column(length = 500)
    private String fotoUrl;

    @PrePersist
    protected void onCreate() {
        this.dataRegistro = LocalDateTime.now();
    }
}

