package cl.duoc.ms_vidasalud_bff.config;

import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadResourceServerHttpSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración de seguridad: valida los JWT emitidos por Azure Entra ID.
 *
 * El BFF actúa como Resource Server, no como cliente: no emite tokens ni
 * redirige a un login. Solo recibe un token en la cabecera Authorization,
 * verifica su firma, emisor, audiencia y vigencia, y deja pasar o rechaza.
 *
 * @EnableMethodSecurity activa la evaluación de @PreAuthorize en los métodos.
 * Por ahora el BFF no restringe por rol, pero queda habilitada para cuando
 * se necesite.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF protege formularios con sesión y cookies. Una API REST sin
                // estado, autenticada por token en una cabecera, no es vulnerable
                // a ese ataque: el navegador no adjunta el token automáticamente.
                .csrf(AbstractHttpConfigurer::disable)

                // CORS va antes que la autenticación: el preflight OPTIONS que
                // manda el navegador no trae token, y si no se responde aquí
                // Spring Security lo rechazaría con 401.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Todo endpoint exige autenticación.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated())

                // Configura el Resource Server al estilo Azure AD. Internamente
                // llama a oauth2ResourceServer().jwt(), pero además registra el
                // conversor de Azure que transforma los claims en autoridades:
                //   roles -> APPROLE_<rol>   (usado por @PreAuthorize)
                //   scp   -> SCOPE_<scope>
                // El JwtDecoder (firma, emisor, audiencia y expiración) es el
                // bean del starter de Azure, armado a partir del application.yaml.
                .with(AadResourceServerHttpSecurityConfigurer.aadResourceServer(),
                        Customizer.withDefaults())

                .build();
    }

    /**
     * Orígenes que el navegador puede usar para llamar a este BFF.
     *
     * No se usan cookies (el token va en la cabecera Authorization), así que
     * allowCredentials queda en su valor por defecto (false).
     */
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
