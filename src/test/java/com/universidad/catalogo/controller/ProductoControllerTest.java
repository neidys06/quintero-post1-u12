package com.universidad.catalogo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba de la CAPA WEB: @WebMvcTest carga solo ProductoController (más
 * filtros y convertidores JSON) y MockMvc simula las peticiones HTTP sin abrir
 * un puerto. El servicio se reemplaza con @MockBean: aquí se prueba el
 * contrato HTTP (rutas, códigos, JSON), no la lógica ni la base de datos.
 */
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProductoService service;

    @Test
    void get_productoExiste_retorna200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(producto(1L, "Laptop", 1500.0, "ELECTRONICA"));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.precio").value(1500.0));
    }

    @Test
    void get_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new EntityNotFoundException("Producto no encontrado: 99"));

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Producto no encontrado: 99"));
    }

    @Test
    void get_listarPorCategoria_retorna200ConLista() throws Exception {
        when(service.listarPorCategoria("ELECTRONICA"))
                .thenReturn(List.of(producto(1L, "Laptop", 1500.0, "ELECTRONICA")));

        mockMvc.perform(get("/api/productos").param("categoria", "ELECTRONICA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoria").value("ELECTRONICA"));
    }

    @Test
    void post_conDatosValidos_retorna201() throws Exception {
        Producto entrada = producto(null, "Mouse", 50.0, "ELECTRONICA");
        when(service.crear(any())).thenReturn(producto(1L, "Mouse", 50.0, "ELECTRONICA"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void post_conPrecioInvalido_retorna400() throws Exception {
        Producto entrada = producto(null, "Auricular", -5.0, "ELECTRONICA");
        when(service.crear(any())).thenThrow(new IllegalArgumentException("El precio debe ser mayor a cero"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El precio debe ser mayor a cero"));
    }

    private Producto producto(Long id, String nombre, double precio, String categoria) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setCategoria(categoria);
        return p;
    }
}
