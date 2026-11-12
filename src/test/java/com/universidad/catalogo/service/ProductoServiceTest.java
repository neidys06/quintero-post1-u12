package com.universidad.catalogo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Prueba UNITARIA de ProductoService: el repositorio se reemplaza por un mock
 * (@Mock) y Mockito lo inyecta en el servicio (@InjectMocks). No hay Spring ni
 * base de datos: solo se verifica la lógica de negocio, en milisegundos.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    ProductoRepository repo;

    @InjectMocks
    ProductoService service;

    @Test
    void crear_conDatosValidos_guardaYRetorna() {
        Producto p = producto("Teclado mecánico", 120.0, "ELECTRONICA");
        when(repo.save(any())).thenReturn(p);

        assertThat(service.crear(p).getNombre()).isEqualTo("Teclado mecánico");
        verify(repo).save(p);
    }

    @Test
    void crear_conPrecioNegativo_lanzaIllegalArgumentException() {
        Producto p = producto("Mouse", -10.0, "ELECTRONICA");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crear(p));

        assertThat(ex.getMessage()).isEqualTo("El precio debe ser mayor a cero");
        // La validación rechaza el dato ANTES de tocar el repositorio
        verify(repo, never()).save(any());
    }

    @Test
    void crear_conNombreVacio_lanzaIllegalArgumentException() {
        Producto p = producto("   ", 50.0, "ELECTRONICA");

        assertThrows(IllegalArgumentException.class, () -> service.crear(p));
        verify(repo, never()).save(any());
    }

    @Test
    void buscarPorId_noExiste_lanzaEntityNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void listarPorCategoria_delegaEnElRepositorio_retornaSuResultado() {
        Producto laptop = producto("Laptop", 1500.0, "ELECTRONICA");
        when(repo.findByCategoria("ELECTRONICA")).thenReturn(List.of(laptop));

        assertThat(service.listarPorCategoria("ELECTRONICA")).containsExactly(laptop);
    }

    @Test
    void actualizarPrecio_conValorValido_actualizaYGuarda() {
        Producto p = producto("Monitor", 100.0, "ELECTRONICA");
        p.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.actualizarPrecio(1L, 150.0).getPrecio()).isEqualTo(150.0);
        verify(repo).save(p);
    }

    @Test
    void actualizarPrecio_conPrecioCero_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.actualizarPrecio(1L, 0.0));
        // Ni siquiera se consulta el producto: la regla se valida primero
        verify(repo, never()).findById(any());
    }

    private Producto producto(String nombre, double precio, String categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setCategoria(categoria);
        return p;
    }
}
