package com.example.redNorte.demo.configTest;

import com.example.redNorte.demo.config.JwtAuthFilter;
import com.example.redNorte.demo.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private JwtService jwtService;
    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        filter = new JwtAuthFilter(jwtService);
        // Limpia el contexto de seguridad antes de cada test
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterWithValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/citas");
        request.addHeader("Authorization", "Bearer validToken");

        when(jwtService.extractCorreo("validToken")).thenReturn("arturo@mail.com");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // Usa el método público doFilter
        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("arturo@mail.com",
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void testDoFilterWithoutToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/citas"); // ruta protegida sin token

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        // Verifica que no se haya autenticado nada
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
