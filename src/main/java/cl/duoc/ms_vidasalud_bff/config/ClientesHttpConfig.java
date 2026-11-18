package cl.duoc.ms_vidasalud_bff.config;

import cl.duoc.ms_vidasalud_bff.client.AppointmentsClient;
import cl.duoc.ms_vidasalud_bff.client.AuditClient;
import cl.duoc.ms_vidasalud_bff.client.CatalogClient;
import cl.duoc.ms_vidasalud_bff.client.ReportClient;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "appointments", types = AppointmentsClient.class)
@ImportHttpServices(group = "catalog", types = CatalogClient.class)
@ImportHttpServices(group = "report", types = ReportClient.class)
@ImportHttpServices(group = "audit", types = AuditClient.class)
public class ClientesHttpConfig {

    /** appointments registra en la auditoría "desde dónde": la IP del navegador, no la del BFF. */
    static final String CABECERA_IP = "X-Forwarded-For";

    @Bean
    RestClientHttpServiceGroupConfigurer tokenYErrores() {
        return groups -> groups.forEachClient((group, builder) -> builder
                .defaultStatusHandler(status -> true, (request, response) -> { })
                .requestInterceptor((request, body, execution) -> {
                    String traceId = MDC.get(TraceIdFilter.MDC_KEY);
                    if (traceId != null) {
                        request.getHeaders().set(TraceIdFilter.HEADER, traceId);
                    }
                    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
                        String token = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                        if (token != null) {
                            request.getHeaders().set(HttpHeaders.AUTHORIZATION, token);
                        }
                        String reenviada = attrs.getRequest().getHeader(CABECERA_IP);
                        String ip = attrs.getRequest().getRemoteAddr();
                        request.getHeaders().set(CABECERA_IP, reenviada == null ? ip : reenviada + ", " + ip);
                    }
                    return execution.execute(request, body);
                }));
    }
}
