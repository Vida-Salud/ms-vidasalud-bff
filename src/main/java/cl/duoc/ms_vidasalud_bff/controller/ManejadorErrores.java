package cl.duoc.ms_vidasalud_bff.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail servicioNoDisponible(ResourceAccessException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "El servicio no está disponible. Intente nuevamente más tarde.");
        problema.setTitle("Servicio no disponible");
        return problema;
    }
}
