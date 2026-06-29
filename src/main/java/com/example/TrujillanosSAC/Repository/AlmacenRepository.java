package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    Optional<Almacen> findFirstByOrderByIdAlmacenAsc();
}
