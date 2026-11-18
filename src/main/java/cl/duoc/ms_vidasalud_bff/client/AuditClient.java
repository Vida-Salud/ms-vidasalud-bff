package cl.duoc.ms_vidasalud_bff.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "/api/v1/audit", accept = MediaType.APPLICATION_JSON_VALUE)
public interface AuditClient {

    @GetExchange
    ResponseEntity<String> buscar(@RequestParam(required = false) String usuario,
                                  @RequestParam(required = false) String type,
                                  @RequestParam(required = false) Long atencionId,
                                  @RequestParam(required = false) String from,
                                  @RequestParam(required = false) String to);

    @GetExchange("/appointments/{atencionId}")
    ResponseEntity<String> timeline(@PathVariable Long atencionId);
}
