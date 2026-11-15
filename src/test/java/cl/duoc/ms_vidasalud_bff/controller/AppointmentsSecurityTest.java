package cl.duoc.ms_vidasalud_bff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica la matriz de autorizacion por rol de SecurityConfig con un
 * JwtDecoder de prueba: el claim "roles" del token se convierte en autoridad
 * APPROLE_<rol> (mapeo por defecto de spring-cloud-azure-starter-active-directory),
 * exactamente como en produccion.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(AppointmentsSecurityTest.JwtDecoderTestConfig.class)
class AppointmentsSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenConRolNoPermitido_devuelve403() throws Exception {
        // Auditor no esta en la lista permitida para GET /api/appointments.
        mockMvc.perform(get("/api/v1/appointments")
                        .header("Authorization", "Bearer token-auditor"))
                .andExpect(status().isForbidden());
    }

    @Test
    void tokenConRolPermitido_pasaAutorizacionYLlegaAlProxy() throws Exception {
        // Cliente si puede ver appointments. Como ms-vidasalud-appointments no
        // esta levantado en este test, el BFF responde 503 via el
        // ExceptionHandler: eso confirma que la autorizacion se supero y la
        // peticion llego al proxy, no que fue bloqueada por rol o por token.
        mockMvc.perform(get("/api/v1/appointments")
                        .header("Authorization", "Bearer token-cliente")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void clienteNoCambiaEstados() throws Exception {
        mockMvc.perform(put("/api/v1/appointments/1/status")
                        .header("Authorization", "Bearer token-cliente")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\": \"CONFIRMADA\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void operadorCambiaEstadosYLlegaAlServicio() throws Exception {
        mockMvc.perform(put("/api/v1/appointments/1/status")
                        .header("Authorization", "Bearer token-operador")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\": \"CONFIRMADA\"}"))
                .andExpect(status().isServiceUnavailable()); // pasó la seguridad; appointments no responde
    }

    @Test
    void versionNoSoportadaResponde400() throws Exception {
        mockMvc.perform(get("/api/v9/appointments").header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clienteNoVeElCatalogo() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/services").header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isForbidden());
    }

    @TestConfiguration
    static class JwtDecoderTestConfig {

        @Bean
        @Primary
        JwtDecoder testJwtDecoder() {
            return token -> {
                List<String> roles = switch (token) {
                    case "token-cliente" -> List.of("Cliente");
                    case "token-auditor" -> List.of("Auditor");
                    case "token-operador" -> List.of("Operador");
                    default -> List.of();
                };
                Instant now = Instant.now();
                return Jwt.withTokenValue(token)
                        .header("alg", "none")
                        .claim("roles", roles)
                        .claim("sub", "test-user")
                        .issuedAt(now)
                        .expiresAt(now.plusSeconds(300))
                        .build();
            };
        }
    }
}
