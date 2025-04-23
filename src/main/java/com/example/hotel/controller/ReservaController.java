package com.example.hotel.controller;

import com.example.hotel.dto.ReservaRequest;
import com.example.hotel.model.Reservas;
import com.example.hotel.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService service;

    @GetMapping
    public List<Reservas> listarReservas() {
        return service.listarReservas();
    }

    @GetMapping("/{id}")
    public Reservas buscarReservaPorId(@PathVariable Integer id) {
        return service.buscarReservaPorId(id);
    }

    @PostMapping
    public Reservas cadastrarReserva(@RequestBody @Valid ReservaRequest request) {
        return service.cadastrarReserva(request);
    }
}
