package com.example.hotel.service;

import com.example.hotel.exception.DisponibilidadeException;
import com.example.hotel.exception.ValidacaoException;
import com.example.hotel.model.Quarto;
import com.example.hotel.repository.QuartoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuartoService {

    private final QuartoRepository repository;

    public List<Quarto> buscarTodos() {
        return repository.findAll();
    }

    public Quarto buscarQuartoPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Quarto não encontrado"));
    }

    public List<Quarto> obterQuartosDisponiveis() {

        Boolean disponibilidade = true;

        List<Quarto> quartosDisponiveis = repository.findByDisponibilidade(disponibilidade);

        if (quartosDisponiveis.isEmpty()) {
            throw new DisponibilidadeException("Nenhum quarto disponível no momento");
        }

        return quartosDisponiveis;
    }

}
