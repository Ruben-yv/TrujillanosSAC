package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DETALLE_MOV_INSUMO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleMovInsumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE_INSUMO")
    private Long idDetalleInsumo;

    @NotNull
    @Column(name = "CANTIDAD", nullable = false)
    private Integer cantidad;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MOVIMIENTO", nullable = false)
    private Movimiento movimiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INSUMO", nullable = false)
    private Insumo insumo;
}
