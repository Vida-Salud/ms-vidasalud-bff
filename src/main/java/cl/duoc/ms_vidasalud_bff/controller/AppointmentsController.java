package cl.duoc.ms_vidasalud_bff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * Proxy hacia ms-vidasalud-appointments.
 *
 * Traduce /api/appointments (BFF) a /api/atenciones (appointments) y reenvía el
 * header Authorization tal como llega. Las reglas por rol ya se aplicaron en el
 * SecurityFilterChain antes de llegar aquí.
 *
 * Los bodies viajan como String sin interpretarlos: el BFF no valida, eso lo
 * hace appointments. Sus respuestas (incluidos 400 y 404) se devuelven con el
 * mismo status y body.
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentsController {

    @Autowired
    private RestClient appointmentsRestClient;

    public record StatusRequest(String status) {
    }

    public record CambiarEstadoBody(String nuevoEstado) {
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Token válido para la página de Atenciones");
    }

    @GetMapping
    public ResponseEntity<String> listar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return reenviar(appointmentsRestClient.get()
                .uri("/api/atenciones")
                .header(HttpHeaders.AUTHORIZATION, authorization));
    }

    @PostMapping
    public ResponseEntity<String> crear(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody String body) {
        return reenviar(appointmentsRestClient.post()
                .uri("/api/atenciones")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> buscarPorId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable Long id) {
        return reenviar(appointmentsRestClient.get()
                .uri("/api/atenciones/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable Long id,
            @RequestBody String body) {
        return reenviar(appointmentsRestClient.put()
                .uri("/api/atenciones/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> cambiarEstado(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable Long id,
            @RequestBody StatusRequest request) {
        return reenviar(appointmentsRestClient.put()
                .uri("/api/atenciones/{id}/estado", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CambiarEstadoBody(request.status())));
    }

    private ResponseEntity<String> reenviar(RestClient.RequestHeadersSpec<?> peticion) {
        ResponseEntity<String> respuesta = peticion.retrieve()
                .onStatus(status -> true, (req, res) -> { })
                .toEntity(String.class);

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(respuesta.getStatusCode());
        MediaType contentType = respuesta.getHeaders().getContentType();
        if (contentType != null) {
            builder.contentType(contentType);
        }
        return builder.body(respuesta.getBody());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail appointmentsNoDisponible(ResourceAccessException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "El servicio de atenciones no está disponible. Intente nuevamente más tarde.");
        problema.setTitle("Servicio no disponible");
        return problema;
    }
}
