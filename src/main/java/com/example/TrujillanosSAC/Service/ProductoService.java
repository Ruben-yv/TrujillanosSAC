package com.example.TrujillanosSAC.Service;

import com.example.TrujillanosSAC.Model.Producto;

import java.util.List;

public interface ProductoService {
    List<Producto> listarProductos();

    Producto obtenerProductoPorId(Long id);

    List<Producto> listarPresentacionesDelProducto(Long id);
}
