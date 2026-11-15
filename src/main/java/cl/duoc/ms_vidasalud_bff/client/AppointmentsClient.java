package cl.duoc.ms_vidasalud_bff.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange(url = "/api/v1/appointments", accept = MediaType.APPLICATION_JSON_VALUE)
public interface AppointmentsClient {

    @GetExchange
    ResponseEntity<String> listar(@RequestParam(required = false) String status,
                                  @RequestParam(required = false) String from,
                                  @RequestParam(required = false) String to);

    @GetExchange("/{id}")
    ResponseEntity<String> buscar(@PathVariable Long id);

    @PostExchange(contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> crear(@RequestBody String body);

    @PutExchange(url = "/{id}", contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody String body);

    @PutExchange(url = "/{id}/status", contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> cambiarEstado(@PathVariable Long id, @RequestBody String body);
}
