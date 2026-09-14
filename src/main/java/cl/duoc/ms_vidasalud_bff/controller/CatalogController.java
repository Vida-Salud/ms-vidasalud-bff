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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

	@Autowired
	private RestClient catalogRestClient;


	@PostMapping("/services")
	public ResponseEntity<String> crearServicio(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestBody String body) {
		return reenviar(catalogRestClient.post()
				.uri("/api/catalog/services")
				.header(HttpHeaders.AUTHORIZATION, authorization)
				.contentType(MediaType.APPLICATION_JSON)
				.body(body));
	}

	@GetMapping("/services")
	public ResponseEntity<String> listarServicios(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
		return reenviar(catalogRestClient.get()
				.uri("/api/catalog/services")
				.header(HttpHeaders.AUTHORIZATION, authorization));
	}

	@GetMapping("/services/{id}")
	public ResponseEntity<String> obtenerServicio(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@PathVariable Long id) {
		return reenviar(catalogRestClient.get()
				.uri("/api/catalog/services/{id}", id)
				.header(HttpHeaders.AUTHORIZATION, authorization));
	}

	@PutMapping("/services/{id}")
	public ResponseEntity<String> actualizarServicio(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@PathVariable Long id,
			@RequestBody String body) {
		return reenviar(catalogRestClient.put()
				.uri("/api/catalog/services/{id}", id)
				.header(HttpHeaders.AUTHORIZATION, authorization)
				.contentType(MediaType.APPLICATION_JSON)
				.body(body));
	}


	@PostMapping("/boxes")
	public ResponseEntity<String> crearBox(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestBody String body) {
		return reenviar(catalogRestClient.post()
				.uri("/api/catalog/boxes")
				.header(HttpHeaders.AUTHORIZATION, authorization)
				.contentType(MediaType.APPLICATION_JSON)
				.body(body));
	}

	@GetMapping("/boxes")
	public ResponseEntity<String> listarBoxes(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestParam(required = false) Long servicioId) {

		if (servicioId != null) {
			return reenviar(catalogRestClient.get()
					.uri("/api/catalog/boxes?servicioId={id}", servicioId)
					.header(HttpHeaders.AUTHORIZATION, authorization));
		}
		return reenviar(catalogRestClient.get()
				.uri("/api/catalog/boxes")
				.header(HttpHeaders.AUTHORIZATION, authorization));
	}

	@GetMapping("/boxes/{id}")
	public ResponseEntity<String> obtenerBox(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@PathVariable Long id) {
		return reenviar(catalogRestClient.get()
				.uri("/api/catalog/boxes/{id}", id)
				.header(HttpHeaders.AUTHORIZATION, authorization));
	}


	@GetMapping("/cupos")
	public ResponseEntity<String> listarCupos(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestParam(required = false) LocalDate fecha) {

		if (fecha != null) {
			return reenviar(catalogRestClient.get()
					.uri("/api/catalog/cupos?fecha={fecha}", fecha)
					.header(HttpHeaders.AUTHORIZATION, authorization));
		}
		return reenviar(catalogRestClient.get()
				.uri("/api/catalog/cupos")
				.header(HttpHeaders.AUTHORIZATION, authorization));
	}

	@GetMapping("/cupos/box/{boxId}/fecha/{fecha}")
	public ResponseEntity<String> obtenerCupoPorBoxYFecha(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@PathVariable Long boxId,
			@PathVariable LocalDate fecha) {
		return reenviar(catalogRestClient.get()
				.uri("/api/catalog/cupos/box/{boxId}/fecha/{fecha}", boxId, fecha)
				.header(HttpHeaders.AUTHORIZATION, authorization));
	}

	@PutMapping("/cupos/{id}")
	public ResponseEntity<String> actualizarCupo(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@PathVariable Long id,
			@RequestBody String body) {
		return reenviar(catalogRestClient.put()
				.uri("/api/catalog/cupos/{id}", id)
				.header(HttpHeaders.AUTHORIZATION, authorization)
				.contentType(MediaType.APPLICATION_JSON)
				.body(body));
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
	public ProblemDetail catalogNoDisponible(ResourceAccessException e) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
				"El servicio de catálogo no está disponible. Intente nuevamente más tarde.");
		problema.setTitle("Servicio no disponible");
		return problema;
	}
}
