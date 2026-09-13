package cl.duoc.ms_vidasalud_bff.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/bff")
public class BffStatusController {

    public record DataDto(LocalDateTime timestamp, String message) {
    }

    @GetMapping("/status")
    public ResponseEntity<DataDto> getStatus() {
        DataDto data = new DataDto(LocalDateTime.now(), "Hello, World!");
        return ResponseEntity.ok(data);
    }
}
