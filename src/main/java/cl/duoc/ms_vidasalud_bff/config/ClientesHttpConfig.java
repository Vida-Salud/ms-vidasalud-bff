package cl.duoc.ms_vidasalud_bff.config;

import cl.duoc.ms_vidasalud_bff.client.AppointmentsClient;
import cl.duoc.ms_vidasalud_bff.client.CatalogClient;
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
public class ClientesHttpConfig {

    @Bean
    RestClientHttpServiceGroupConfigurer tokenYErrores() {
        return groups -> groups.forEachClient((group, builder) -> builder
                .defaultStatusHandler(status -> true, (request, response) -> { })
                .requestInterceptor((request, body, execution) -> {
                    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
                        String token = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                        if (token != null) {
                            request.getHeaders().set(HttpHeaders.AUTHORIZATION, token);
                        }
                    }
                    return execution.execute(request, body);
                }));
    }
}
