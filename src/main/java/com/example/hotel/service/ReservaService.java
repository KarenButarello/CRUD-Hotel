package com.example.hotel.service;

import com.example.hotel.dto.ReservaRequest;
import com.example.hotel.dto.ReservaResponse;
import com.example.hotel.exception.DisponibilidadeException;
import com.example.hotel.exception.ValidacaoException;
import com.example.hotel.model.Quarto;
import com.example.hotel.model.Reservas;
import com.example.hotel.repository.HospedeRepository;
import com.example.hotel.repository.QuartoRepository;
import com.example.hotel.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository repository;
    private final QuartoRepository quartoRepository;
    private final HospedeRepository hospedeRepository;

    public List<ReservaResponse> listarReservas() {
        List<Reservas> reservas = repository.findAll();
        if (reservas.isEmpty()) {
            throw new NoSuchElementException("Nenhuma reserva encontrada no sistema.");
        }

        return reservas.stream()
                .map(ReservaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ReservaResponse buscarReservaPorId(Integer id) {
        Reservas reserva = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reserva não encontrada"));

        return ReservaResponse.fromEntity(reserva);
    }

    public ReservaResponse cadastrarReserva(ReservaRequest request) {
        var reserva = new Reservas();
        reserva.setCheckin(request.getCheckin());
        reserva.setCheckout(request.getCheckout());
        reserva.setQtdHospedes(request.getQtdHospedes());

        var hospede = hospedeRepository.findById(request.getHospedeId())
                .orElseThrow(() -> new NoSuchElementException("Hospede não encontrado"));
        reserva.setHospede(hospede);

        var quarto = quartoRepository.findById(request.getQuartoId())
                .orElseThrow(() -> new NoSuchElementException("Quarto não encontrado"));

        reserva.setQuarto(quarto);

        try {
            validarPeriodo(request.getCheckin(), request.getCheckout());
            validarDisponibilidadeQuarto(quarto);
            validarQuantidadeHospedePorQuarto(quarto, request.getQtdHospedes());
            reserva.setSituacao(true);
            quarto.setDisponibilidade(false);
            quartoRepository.save(quarto);
        } catch (DisponibilidadeException e) {
            throw new DisponibilidadeException("O quarto não está disponível para reserva.");
        }

        Reservas reservaSalva = repository.save(reserva);
        return ReservaResponse.fromEntity(reservaSalva);
    }

    private void validarDisponibilidadeQuarto(Quarto quarto) {
        if (quarto.getDisponibilidade() != null && !quarto.getDisponibilidade()) {
            throw new DisponibilidadeException("O quarto não está disponível para reserva.");
        }
    }

    private void validarQuantidadeHospedePorQuarto(Quarto quarto, Integer quantidadeHospedes) {
        if (quarto.getQtdHospedes() < quantidadeHospedes) {
            throw new ValidacaoException("Quantidade de hospedes superior a capacidade do quarto");
        }
    }

    private void validarPeriodo(LocalDate checkin, LocalDate checkout) {
        if (checkin.isAfter(checkout)) {
            throw new ValidacaoException("A data de checkin não pode ser depois da data de checkout");
        }
    }
}