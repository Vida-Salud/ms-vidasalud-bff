package cl.duoc.ms_vidasalud_bff.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/** El único lugar que convierte la respuesta de un servicio en la respuesta del BFF. */
public final class Reenvio {

    private Reenvio() {
    }

    /** Copia estado, Content-Type y body; no copia Transfer-Encoding ni Content-Length, que son de esa conexión. */
    public static ResponseEntity<String> respuesta(ResponseEntity<String> delServicio) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(delServicio.getStatusCode());
        MediaType tipo = delServicio.getHeaders().getContentType();
        if (tipo != null) {
            builder.contentType(tipo);
        }
        return builder.body(delServicio.getBody());
    }
}
