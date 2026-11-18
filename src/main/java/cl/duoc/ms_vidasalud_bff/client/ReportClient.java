package cl.duoc.ms_vidasalud_bff.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "/api/v1/report", accept = MediaType.APPLICATION_JSON_VALUE)
public interface ReportClient {

    @GetExchange("/kpis")
    ResponseEntity<String> kpis(@RequestParam(required = false) String range);

    @GetExchange("/top-services")
    ResponseEntity<String> serviciosMasDemandados(@RequestParam(required = false) String range,
                                                  @RequestParam(required = false) Integer limit);
}
