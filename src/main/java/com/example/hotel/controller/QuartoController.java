package com.example.hotel.controller;

import com.example.hotel.model.Quarto;
import com.example.hotel.service.QuartoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/quarto")
public class QuartoController {

    @Autowired
    private final QuartoService service;

    @GetMapping
    public List<Quarto> buscarQuarto() {
        return service.buscarTodos();
    }

    @GetMapping("/{id}")
    public Quarto buscarQuartoPorId(@PathVariable Integer id) {
        return service.buscarQuartoPorId(id);
    }

    @GetMapping("/disponiveis")
    public List<Quarto> obterQuartosDisponiveis(Boolean disponibilidade) {
        return service.obterQuartosDisponiveis();
    }
}
