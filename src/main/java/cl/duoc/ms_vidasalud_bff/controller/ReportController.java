package cl.duoc.ms_vidasalud_bff.controller;

import cl.duoc.ms_vidasalud_bff.client.ReportClient;
import cl.duoc.ms_vidasalud_bff.client.Reenvio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Reenvía a ms-vidasalud-report. El rol (Admin) ya se exigió en el SecurityFilterChain. */
@RestController
@RequestMapping("/api/{version}/report")
public class ReportController {

    private final ReportClient report;

    public ReportController(ReportClient report) {
        this.report = report;
    }

    @GetMapping("/kpis")
    public ResponseEntity<String> kpis(@RequestParam(required = false) String range) {
        return Reenvio.respuesta(report.kpis(range));
    }

    @GetMapping("/top-services")
    public ResponseEntity<String> serviciosMasDemandados(@RequestParam(required = false) String range,
                                                         @RequestParam(required = false) Integer limit) {
        return Reenvio.respuesta(report.serviciosMasDemandados(range, limit));
    }
}
