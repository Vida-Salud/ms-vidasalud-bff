package cl.duoc.ms_vidasalud_bff.controller;

import cl.duoc.ms_vidasalud_bff.client.AppointmentsClient;
import cl.duoc.ms_vidasalud_bff.client.Reenvio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Traduce las rutas del BFF a las de ms-vidasalud-appointments. El token lo copia el cliente HTTP
 * (ClientesHttpConfig) y las reglas por rol ya se aplicaron en el SecurityFilterChain.
 * Los bodies viajan como String sin interpretarlos: el BFF no valida, eso lo hace appointments.
 */
@RestController
@RequestMapping("/api/{version}/appointments")
public class AppointmentsController {

    private final AppointmentsClient appointments;

    public AppointmentsController(AppointmentsClient appointments) {
        this.appointments = appointments;
    }

    @GetMapping
    public ResponseEntity<String> listar(@RequestParam(required = false) String status,
                                         @RequestParam(required = false) String from,
                                         @RequestParam(required = false) String to) {
        return Reenvio.respuesta(appointments.listar(status, from, to));
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody String body) {
        return Reenvio.respuesta(appointments.crear(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> buscarPorId(@PathVariable Long id) {
        return Reenvio.respuesta(appointments.buscar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody String body) {
        return Reenvio.respuesta(appointments.actualizar(id, body));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> cambiarEstado(@PathVariable Long id, @RequestBody String body) {
        return Reenvio.respuesta(appointments.cambiarEstado(id, body));
    }
}
