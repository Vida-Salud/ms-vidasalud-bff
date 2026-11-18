package cl.duoc.ms_vidasalud_bff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Matriz de roles de reportería y auditoría en el BFF. Un 503 significa que la petición pasó la seguridad
 * y llegó al cliente HTTP (report y audit no están levantados en el test).
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(AppointmentsSecurityTest.JwtDecoderTestConfig.class)
class ReportAuditSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void reporteriaSoloParaAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/report/kpis").header("Authorization", "Bearer token-operador"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/report/kpis").header("Authorization", "Bearer token-auditor"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/report/kpis").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void auditoriaParaAdminYAuditor() throws Exception {
        mockMvc.perform(get("/api/v1/audit").header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/audit").header("Authorization", "Bearer token-auditor"))
                .andExpect(status().isServiceUnavailable());
        mockMvc.perform(get("/api/v1/audit/appointments/42").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void laAuditoriaEsSoloLectura() throws Exception {
        mockMvc.perform(post("/api/v1/audit").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isForbidden());   // denyAll para todo lo que no es GET
    }
}
