package cl.duoc.ms_vidasalud_bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient appointmentsRestClient(
            @Value("${vidasalud.services.appointments-url}") String appointmentsUrl) {
        return RestClient.builder()
                .baseUrl(appointmentsUrl)
                .build();
    }
}
