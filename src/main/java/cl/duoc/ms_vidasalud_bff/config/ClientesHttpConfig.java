package cl.duoc.ms_vidasalud_bff.config;

import cl.duoc.ms_vidasalud_bff.client.AppointmentsClient;
import cl.duoc.ms_vidasalud_bff.client.AuditClient;
import cl.duoc.ms_vidasalud_bff.client.CatalogClient;
import cl.duoc.ms_vidasalud_bff.client.ReportClient;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
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
    RestClientHttpServiceGroupConfigurer tokenYErrores(
            @Value("${vidasalud.proxies-de-confianza:0}") int proxiesDeConfianza) {
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
                        request.getHeaders().set(CABECERA_IP, ipDelCliente(attrs.getRequest(), proxiesDeConfianza));
                    }
                    return execution.execute(request, body);
                }));
    }

    /**
     * IP del cliente que el BFF le informa a los servicios de abajo, como un único valor que determina él.
     * <p>
     * No se reenvía la cabecera {@code X-Forwarded-For} que mandó el cliente: cualquiera puede inventarla
     * (por ejemplo {@code X-Forwarded-For: 8.8.8.8}) y terminaría grabada en {@code AUD_EVENTO}.
     * <p>
     * Cada proxy agrega al final de la cabecera la dirección de quien le habló. Por eso, con N proxies de
     * confianza delante del BFF, el cliente real es la entrada número N contando desde el final; lo que esté
     * más a la izquierda lo pudo escribir el propio cliente y se descarta. Sin proxies de confianza (o si la
     * cadena es más corta de lo esperado), se usa la IP de la conexión.
     *
     * @param proxiesDeConfianza cuántos proxies hay delante del BFF ({@code vidasalud.proxies-de-confianza}):
     *                           0 en local, 1 en AWS detrás del API Gateway
     */
    static String ipDelCliente(HttpServletRequest request, int proxiesDeConfianza) {
        if (proxiesDeConfianza > 0) {
            String cabecera = request.getHeader(CABECERA_IP);
            if (cabecera != null) {
                String[] valores = cabecera.split(",");
                int indice = valores.length - proxiesDeConfianza;
                if (indice >= 0 && indice < valores.length && !valores[indice].isBlank()) {
                    return valores[indice].trim();
                }
            }
        }
        return request.getRemoteAddr();
    }
}
