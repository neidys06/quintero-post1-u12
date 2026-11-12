package com.universidad.catalogo.repository;

import com.universidad.catalogo.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Consulta derivada: Spring Data genera el SELECT ... WHERE categoria = ?
    List<Producto> findByCategoria(String categoria);
}
