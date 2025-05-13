package com.example.hotel.service;

import com.example.hotel.dto.CancelamentoResponse;
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
import org.springframework.http.ResponseEntity;
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

    public ReservaResponse atualizarReserva(Integer id, ReservaRequest request) {
        var reservaAtual = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Reserva não encontrada"));

        if (request.getCheckin() != null) {
            LocalDate checkinParaValidar = request.getCheckin();
            LocalDate checkoutParaValidar = request.getCheckout() != null
                    ? request.getCheckout()
                    : reservaAtual.getCheckout();

            validarPeriodo(checkinParaValidar, checkoutParaValidar);
            reservaAtual.setCheckin(request.getCheckin());
        }

        if (request.getCheckout() != null) {
            LocalDate checkoutParaValidar = request.getCheckout();
            LocalDate checkinParaValidar = request.getCheckin() != null
                    ? request.getCheckin()
                    : reservaAtual.getCheckin();

            validarPeriodo(checkinParaValidar, checkoutParaValidar);
            reservaAtual.setCheckout(request.getCheckout());
        }

        if (request.getQtdHospedes() != null) {
            Quarto quarto = request.getQuartoId() != null
                    ? quartoRepository.findById(request.getQuartoId())
                    .orElseThrow(() -> new NoSuchElementException("Quarto não encontrado"))
                    : reservaAtual.getQuarto();

            validarQuantidadeHospedePorQuarto(quarto, request.getQtdHospedes());

            reservaAtual.setQtdHospedes(request.getQtdHospedes());

            if (request.getQuartoId() != null) {
                Quarto quartoAntigo = reservaAtual.getQuarto();
                quartoAntigo.setDisponibilidade(true);
                quartoRepository.save(quartoAntigo);

                quarto.setDisponibilidade(false);
                reservaAtual.setQuarto(quarto);
                quartoRepository.save(quarto);
            }
        }

        var reservaAtualizada = repository.save(reservaAtual);
        return ReservaResponse.fromEntity(reservaAtualizada);
    }

    public ResponseEntity<CancelamentoResponse> cancelarReserva(Integer id) {
        var reserva = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reserva não encontrada"));

        validarCancelamento(reserva);

        if (Boolean.FALSE.equals(reserva.getSituacao())) {
            throw new ValidacaoException("Reserva já está cancelada");
        }

        var quarto = reserva.getQuarto();
        quarto.setDisponibilidade(true);
        quartoRepository.save(quarto);

        reserva.setSituacao(false);
        var reservaCancelada = repository.save(reserva);

        var response = new CancelamentoResponse(reservaCancelada);

        return ResponseEntity.ok(response);
    }

    private void validarCancelamento(Reservas reserva) {
        if (reserva.getCheckout().isBefore(LocalDate.now())) {
            throw new ValidacaoException("Não é possível cancelar reservas já finalizadas");
        }
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