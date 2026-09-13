package cl.duoc.ms_vidasalud_bff.config;

import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadResourceServerHttpSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Atenciones
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/{id}/status")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador")
                        .requestMatchers(HttpMethod.POST, "/api/appointments")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador", "APPROLE_Cliente")
                        .requestMatchers(HttpMethod.GET, "/api/appointments")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador", "APPROLE_Cliente")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/{id}")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador", "APPROLE_Cliente")
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/{id}")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador")

                        .requestMatchers(HttpMethod.GET, "/api/catalog/**")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador")
                        .requestMatchers(HttpMethod.POST, "/api/catalog/**")
                                .hasAuthority("APPROLE_Admin")
                        .requestMatchers(HttpMethod.PUT, "/api/catalog/**")
                                .hasAuthority("APPROLE_Admin")

                        .requestMatchers(HttpMethod.GET, "/api/report/**")
                                .hasAuthority("APPROLE_Admin")
                        .requestMatchers(HttpMethod.GET, "/api/audit/**")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Auditor")

                        .requestMatchers("/api/appointments/**", "/api/catalog/**",
                                "/api/report/**", "/api/audit/**").denyAll()

                        .anyRequest().authenticated())
                .with(AadResourceServerHttpSecurityConfigurer.aadResourceServer(),
                        Customizer.withDefaults())

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
