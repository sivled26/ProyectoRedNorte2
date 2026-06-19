package BFF.demo.securityTest;

import BFF.demo.security.JwtAuthFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private JwtAuthFilter jwtAuthFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    private final String SECRET_KEY = "rednorteSecretKey12345678901234567890abcd";

    @BeforeEach
    void setUp() {
        jwtAuthFilter = new JwtAuthFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        // Limpiamos el contexto de seguridad antes de cada test para evitar contaminación
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Limpieza al finalizar
        SecurityContextHolder.clearContext();
    }

    // Método utilitario para generar un token JWT válido idéntico al que espera el filtro
    private String generarTokenPrueba(String sujeto) {
        return Jwts.builder()
                .setSubject(sujeto)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void doFilterInternal_DebeIgnorarRutasPublicas_Login() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/bff/pacientes/login");

        // Act
        //  Por esto:
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_DebeIgnorarRutasPublicas_Registro() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/bff/pacientes/registro");

        // Act
        //  Por esto:
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_DebeContinuarFiltro_SiNoTieneHeaderAuthorization() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/bff/citas/listar/juan@correo.com");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);


        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_DebeContinuarFiltro_SiHeaderNoEmpiezaConBearer() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/bff/citas/listar/juan@correo.com");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNz");

        // Act
        //  Por esto:
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_DebeAutenticar_ConTokenValido() throws Exception {
        // Arrange
        String correoEsperado = "paciente@rednorte.com";
        String tokenValido = generarTokenPrueba(correoEsperado);

        when(request.getRequestURI()).thenReturn("/bff/citas/agendar");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + tokenValido);

        // Act
        //  Por esto:
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(correoEsperado, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_DebeRetornar410Unauthorized_ConTokenInvalido() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/bff/citas/agendar");
        // Mandamos un token corrupto explícitamente
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token-totalmente-invalido-y-falso");

        // Act
        //  Por esto:
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        // El filtro debió cortar la cadena de ejecución (no llama a doFilter)
        verify(filterChain, never()).doFilter(request, response);
        // Debe responder con un estado 401
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
