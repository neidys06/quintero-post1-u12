package com.universidad.catalogo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.universidad.catalogo.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Prueba de INTEGRACIÓN del repositorio: @DataJpaTest levanta solo la capa de
 * persistencia (entidades, repositorios, H2 en memoria) y hace rollback al
 * final de cada test, así setUp() puede insertar los mismos datos cada vez.
 */
@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    ProductoRepository repo;

    @Autowired
    TestEntityManager em;

    @BeforeEach
    void setUp() {
        em.persistAndFlush(crear("Laptop", 1500.0, "ELECTRONICA"));
        em.persistAndFlush(crear("Cuaderno", 5.0, "PAPELERIA"));
    }

    @Test
    void findByCategoria_electronica_retornaUnProducto() {
        assertThat(repo.findByCategoria("ELECTRONICA"))
                .hasSize(1)
                .extracting("nombre").containsExactly("Laptop");
    }

    @Test
    void findByCategoria_sinCoincidencias_retornaListaVacia() {
        assertThat(repo.findByCategoria("JUGUETERIA")).isEmpty();
    }

    @Test
    void save_productoNuevo_asignaIdGenerado() {
        Producto guardado = repo.save(crear("Lápiz", 1.5, "PAPELERIA"));

        assertThat(guardado.getId()).isNotNull();
        assertThat(repo.findByCategoria("PAPELERIA")).hasSize(2);
    }

    private Producto crear(String nombre, double precio, String categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setCategoria(categoria);
        return p;
    }
}
