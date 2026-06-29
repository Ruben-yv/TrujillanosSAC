package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("""
            select p
            from Producto p
            left join fetch p.presentacion
            left join fetch p.categoria
            where p.presentacion.idPresentacion = 1
            order by p.nombre
            """)
    List<Producto> findAllConPresentacion();

    @Query("""
            select p
            from Producto p
            left join fetch p.presentacion
            left join fetch p.categoria
            order by p.idProducto
            """)
    List<Producto> findAllParaGestion();

    @Query("select p from Producto p left join fetch p.presentacion where p.idProducto = :id")
    Optional<Producto> findByIdConPresentacion(@Param("id") Long id);

    @Query("""
            select p
            from Producto p
            left join fetch p.presentacion
            where p.nombre = :nombre
            order by p.presentacion.idPresentacion
            """)
    List<Producto> findByNombreConPresentacion(@Param("nombre") String nombre);
}
