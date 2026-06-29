package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @Query("""
            select p
            from Pedido p
            join fetch p.estado
            where p.cliente.idPersona = (
                select u.persona.idPersona
                from Usuario u
                where u.username = :username
            )
            order by p.fechaEmision desc, p.hora desc
            """)
    List<Pedido> findByUsername(@Param("username") String username);

    @Query("""
            select p
            from Pedido p
            join fetch p.estado
            join fetch p.cliente c
            join fetch c.persona
            order by p.fechaEmision desc, p.hora desc
            """)
    List<Pedido> findAllParaGestion();
}
