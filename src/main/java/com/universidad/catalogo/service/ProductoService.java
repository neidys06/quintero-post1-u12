package com.universidad.catalogo.service;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/** Reglas de negocio del catálogo (lo que prueba ProductoServiceTest con Mockito). */
@Service
public class ProductoService {

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public Producto crear(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        validarPrecio(producto.getPrecio());
        return repo.save(producto);
    }

    // Lanza EntityNotFoundException en vez de devolver null: el contrato es
    // explícito y cada llamador no tiene que acordarse de chequear null
    public Producto buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));
    }

    public List<Producto> listarPorCategoria(String categoria) {
        return repo.findByCategoria(categoria);
    }

    public Producto actualizarPrecio(Long id, double nuevoPrecio) {
        validarPrecio(nuevoPrecio);
        Producto p = buscarPorId(id);
        p.setPrecio(nuevoPrecio);
        return repo.save(p);
    }

    // La misma regla en un solo lugar para crear y actualizar
    private void validarPrecio(double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
    }
}
