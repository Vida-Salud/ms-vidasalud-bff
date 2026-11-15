package cl.duoc.ms_vidasalud_bff.config;

import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadResourceServerHttpSecurityConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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
@EnableConfigurationProperties(CorsProperties.class)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final String API = "/api/{version}";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Atenciones
                        .requestMatchers(HttpMethod.PUT, API + "/appointments/{id}/status")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador")
                        .requestMatchers(HttpMethod.POST, API + "/appointments")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador", "APPROLE_Cliente")
                        .requestMatchers(HttpMethod.GET, API + "/appointments")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador", "APPROLE_Cliente")
                        .requestMatchers(HttpMethod.GET, API + "/appointments/{id}")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador", "APPROLE_Cliente")
                        .requestMatchers(HttpMethod.PUT, API + "/appointments/{id}")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador")

                        .requestMatchers(HttpMethod.GET, API + "/catalog/**")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Operador")
                        .requestMatchers(HttpMethod.POST, API + "/catalog/**")
                                .hasAuthority("APPROLE_Admin")
                        .requestMatchers(HttpMethod.PUT, API + "/catalog/**")
                                .hasAuthority("APPROLE_Admin")

                        .requestMatchers(HttpMethod.GET, API + "/report/**")
                                .hasAuthority("APPROLE_Admin")
                        .requestMatchers(HttpMethod.GET, API + "/audit/**")
                                .hasAnyAuthority("APPROLE_Admin", "APPROLE_Auditor")

                        .requestMatchers(API + "/appointments/**", API + "/catalog/**",
                                API + "/report/**", API + "/audit/**").denyAll()

                        .anyRequest().authenticated())
                .with(AadResourceServerHttpSecurityConfigurer.aadResourceServer(),
                        Customizer.withDefaults())

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(CorsProperties cors) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(cors.allowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
        config.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
