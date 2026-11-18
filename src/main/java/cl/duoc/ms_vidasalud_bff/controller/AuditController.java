package cl.duoc.ms_vidasalud_bff.controller;

import cl.duoc.ms_vidasalud_bff.client.AuditClient;
import cl.duoc.ms_vidasalud_bff.client.Reenvio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Reenvía a ms-vidasalud-audit. Los roles (Admin, Auditor) ya se exigieron en el SecurityFilterChain. */
@RestController
@RequestMapping("/api/{version}/audit")
public class AuditController {

    private final AuditClient audit;

    public AuditController(AuditClient audit) {
        this.audit = audit;
    }

    @GetMapping
    public ResponseEntity<String> buscar(@RequestParam(required = false) String usuario,
                                         @RequestParam(required = false) String type,
                                         @RequestParam(required = false) Long atencionId,
                                         @RequestParam(required = false) String from,
                                         @RequestParam(required = false) String to) {
        return Reenvio.respuesta(audit.buscar(usuario, type, atencionId, from, to));
    }

    @GetMapping("/appointments/{atencionId}")
    public ResponseEntity<String> timeline(@PathVariable Long atencionId) {
        return Reenvio.respuesta(audit.timeline(atencionId));
    }
}
