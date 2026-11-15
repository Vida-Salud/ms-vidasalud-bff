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

@HttpExchange(url = "/api/v1/catalog", accept = MediaType.APPLICATION_JSON_VALUE)
public interface CatalogClient {

    @GetExchange("/services")
    ResponseEntity<String> listarServicios();

    @PostExchange(url = "/services", contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> crearServicio(@RequestBody String body);

    @GetExchange("/services/{id}")
    ResponseEntity<String> obtenerServicio(@PathVariable Long id);

    @PutExchange(url = "/services/{id}", contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> actualizarServicio(@PathVariable Long id, @RequestBody String body);

    @GetExchange("/boxes")
    ResponseEntity<String> listarBoxes(@RequestParam(required = false) Long servicioId);

    @PostExchange(url = "/boxes", contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> crearBox(@RequestBody String body);

    @GetExchange("/boxes/{id}")
    ResponseEntity<String> obtenerBox(@PathVariable Long id);

    @GetExchange("/slots")
    ResponseEntity<String> listarCupos(@RequestParam(required = false) String date,
                                       @RequestParam(required = false) Long boxId);

    @PutExchange(url = "/slots/{id}", contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> actualizarCupo(@PathVariable Long id, @RequestBody String body);
}
