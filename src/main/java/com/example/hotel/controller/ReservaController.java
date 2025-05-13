package com.example.hotel.controller;

import com.example.hotel.dto.CancelamentoResponse;
import com.example.hotel.dto.ReservaRequest;
import com.example.hotel.dto.ReservaResponse;
import com.example.hotel.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService service;

    @GetMapping
    public List<ReservaResponse> listarReservas() {
        return service.listarReservas();
    }

    @GetMapping("/{id}")
    public ReservaResponse buscarReservaPorId(@PathVariable Integer id) {
        return service.buscarReservaPorId(id);
    }

    @PostMapping
    public ReservaResponse cadastrarReserva(@RequestBody @Valid ReservaRequest request) {
        return service.cadastrarReserva(request);
    }

    @PutMapping("/{id}/atualizar")
    public ReservaResponse atualizarReserva(@PathVariable Integer id, @RequestBody ReservaRequest request) {
        return service.atualizarReserva(id, request);
    }

    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<CancelamentoResponse> cancelarReserva(@PathVariable Integer id) {
        return service.cancelarReserva(id);
    }
}
