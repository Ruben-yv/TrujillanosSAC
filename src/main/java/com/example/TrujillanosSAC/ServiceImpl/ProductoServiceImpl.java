package com.example.TrujillanosSAC.ServiceImpl;

import com.example.TrujillanosSAC.Model.Producto;
import com.example.TrujillanosSAC.Repository.ProductoRepository;
import com.example.TrujillanosSAC.Service.ProductoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAllConPresentacion();
    }

    @Override
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findByIdConPresentacion(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Override
    public List<Producto> listarPresentacionesDelProducto(Long id) {
        Producto producto = obtenerProductoPorId(id);
        return productoRepository.findByNombreConPresentacion(producto.getNombre());
    }
}
