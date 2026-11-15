package cl.duoc.ms_vidasalud_bff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("vidasalud.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
