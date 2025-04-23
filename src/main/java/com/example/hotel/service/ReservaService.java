package com.example.hotel.service;

import com.example.hotel.dto.ReservaRequest;
import com.example.hotel.exception.DisponibilidadeException;
import com.example.hotel.exception.ValidacaoException;
import com.example.hotel.model.Quarto;
import com.example.hotel.model.Reservas;
import com.example.hotel.repository.HospedeRepository;
import com.example.hotel.repository.QuartoRepository;
import com.example.hotel.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository repository;
    private final QuartoRepository quartoRepository;
    private final HospedeRepository hospedeRepository;

    public List<Reservas> listarReservas() {
        return repository.findAll();
    }

    public Reservas buscarReservaPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ValidacaoException("Reserva não encontrada"));
    }

    public Reservas cadastrarReserva(ReservaRequest request) {
        var reserva = new Reservas();
        reserva.setCheckin(request.getCheckin());
        reserva.setCheckout(request.getCheckout());

        var hospede = hospedeRepository.findById(request.getHospedeId())
                .orElseThrow(() -> new ValidacaoException("Hospede não encontrado"));
        reserva.setHospede(hospede);

        var quarto = quartoRepository.findById(request.getQuartoId())
                .orElseThrow(() -> new ValidacaoException("Quarto não encontrado"));

        reserva.setQuarto(quarto);

        try {
            validarDisponibilidadeQuarto(quarto);
            reserva.setSituacao(true);
            quarto.setDisponibilidade(false);
        } catch (DisponibilidadeException e) {
            reserva.setSituacao(false);
        }

        return repository.save(reserva);
    }

    private void validarDisponibilidadeQuarto(Quarto quarto) {
          if (quarto.getDisponibilidade() != null && !quarto.getDisponibilidade()) {
            throw new DisponibilidadeException("O quarto não está disponível para reserva.");
        }
    }
}