package cl.duoc.ms_vidasalud_bff.controller;

import cl.duoc.ms_vidasalud_bff.client.CatalogClient;
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
 * Traduce las rutas del BFF a las de ms-vidasalud-catalog. El token lo copia el cliente HTTP
 * (ClientesHttpConfig) y las reglas por rol ya se aplicaron en el SecurityFilterChain.
 */
@RestController
@RequestMapping("/api/{version}/catalog")
public class CatalogController {

	private final CatalogClient catalog;

	public CatalogController(CatalogClient catalog) {
		this.catalog = catalog;
	}

	@GetMapping("/services")
	public ResponseEntity<String> listarServicios() {
		return Reenvio.respuesta(catalog.listarServicios());
	}

	@PostMapping("/services")
	public ResponseEntity<String> crearServicio(@RequestBody String body) {
		return Reenvio.respuesta(catalog.crearServicio(body));
	}

	@GetMapping("/services/{id}")
	public ResponseEntity<String> obtenerServicio(@PathVariable Long id) {
		return Reenvio.respuesta(catalog.obtenerServicio(id));
	}

	@PutMapping("/services/{id}")
	public ResponseEntity<String> actualizarServicio(@PathVariable Long id, @RequestBody String body) {
		return Reenvio.respuesta(catalog.actualizarServicio(id, body));
	}

	@GetMapping("/boxes")
	public ResponseEntity<String> listarBoxes(@RequestParam(required = false) Long servicioId) {
		return Reenvio.respuesta(catalog.listarBoxes(servicioId));
	}

	@PostMapping("/boxes")
	public ResponseEntity<String> crearBox(@RequestBody String body) {
		return Reenvio.respuesta(catalog.crearBox(body));
	}

	@GetMapping("/boxes/{id}")
	public ResponseEntity<String> obtenerBox(@PathVariable Long id) {
		return Reenvio.respuesta(catalog.obtenerBox(id));
	}

	@GetMapping("/slots")
	public ResponseEntity<String> listarCupos(@RequestParam(required = false) String date,
			@RequestParam(required = false) Long boxId) {
		return Reenvio.respuesta(catalog.listarCupos(date, boxId));
	}

	@PutMapping("/slots/{id}")
	public ResponseEntity<String> actualizarCupo(@PathVariable Long id, @RequestBody String body) {
		return Reenvio.respuesta(catalog.actualizarCupo(id, body));
	}
}
