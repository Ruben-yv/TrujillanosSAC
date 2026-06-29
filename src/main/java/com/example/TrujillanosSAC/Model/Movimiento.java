package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "MOVIMIENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MOVIMIENTO")
    private Long idMovimiento;

    @NotBlank
    @Column(name = "TIPO_MOVIMIENTO", length = 20, nullable = false)
    private String tipoMovimiento;

    @NotNull
    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(name = "HORA", nullable = false)
    private LocalDateTime hora;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALMACEN", nullable = false)
    private Almacen almacen;
}
