package com.example.hotel.controller;

import com.example.hotel.dto.HospedeRequest;
import com.example.hotel.model.Hospede;

import com.example.hotel.service.HospedeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/hospede")
public class HospedeController {

    @Autowired
    private final HospedeService service;

    @GetMapping("/{id}")
    public Hospede buscarHospede(@PathVariable Integer id) {
        return service.buscarHospedePorId(id);
    }

    @GetMapping("/nome/{nome}")
    public Hospede buscarHospedePorNome(@PathVariable String nome) {
        return service.buscarHospedePorNome(nome);
    }

    @GetMapping
    public List<Hospede> listarTodos(){
        return service.listarTodos();
    }

    @PostMapping
    public Hospede salvarHospede(@RequestBody @Valid HospedeRequest request) {
        return service.salvarHospede(request);
    }

    @PutMapping("/{id}")
    public Hospede atualizarHospede(@PathVariable Integer id, @RequestBody Hospede hospede) {
        return service.atualizarDadosHospede(id, hospede);
    }

    @DeleteMapping("/{id}")
    public void deletarHospede(@PathVariable Integer id) {
        service.deletarHospede(id);
    }
}

