package com.universidad.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de contexto: obliga a Spring a construir TODOS los beans con el perfil
 * dev (H2 en memoria) antes de que el pipeline pase al job de Docker.
 */
@SpringBootTest
@ActiveProfiles("dev")
class ApplicationContextTest {

    @Autowired
    ApplicationContext contexto;

    @Test
    void contextLoads() {
        assertThat(contexto.getEnvironment().getActiveProfiles()).containsExactly("dev");
    }
}
