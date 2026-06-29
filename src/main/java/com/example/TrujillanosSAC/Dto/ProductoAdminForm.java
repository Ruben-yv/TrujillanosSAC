package com.example.TrujillanosSAC.Dto;

import java.math.BigDecimal;

public record ProductoAdminForm(
        String nombre,
        BigDecimal precio,
        String descripcion,
        Integer stock,
        Integer stockMinimo,
        Integer stockLimite,
        Long idCategoria,
        Long idPresentacion
) {
}
