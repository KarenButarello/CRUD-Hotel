package com.example.hotel.dto;

import com.example.hotel.enums.ETipoQuarto;
import com.example.hotel.model.Hospede;
import com.example.hotel.model.Quarto;
import com.example.hotel.model.Reservas;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReservaResponseTest {


    @Test
    void deveConverterReservasParaReservaResponse_comSucesso() {

        var hospede = new Hospede();
        hospede.setId(1);
        hospede.setNome("João Silva");
        hospede.setCpf("123.456.789-10");
        hospede.setTelefone("(11) 99999-9999");
        hospede.setDataNascimento(LocalDate.of(1990, 5, 15));

        var quarto = new Quarto();
        quarto.setId(1);
        quarto.setNumero(101);
        quarto.setDisponibilidade(true);
        quarto.setTipoQuarto(ETipoQuarto.SINGLE);
        quarto.setValor(BigDecimal.valueOf(103));

        var quartoOriginal = new Quarto();
        quartoOriginal.setId(10);
        quartoOriginal.setNumero(101);
        quartoOriginal.setQtdHospedes(2);
        quartoOriginal.setTipoQuarto(ETipoQuarto.SINGLE);
        quartoOriginal.setValor(BigDecimal.valueOf(250.00));
        quartoOriginal.setDisponibilidade(true);

        var reserva = new Reservas();
        reserva.setId(100);
        reserva.setCheckin(LocalDate.of(2024, 12, 15));
        reserva.setCheckout(LocalDate.of(2024, 12, 18));
        reserva.setHospede(hospede);
        reserva.setQuarto(quartoOriginal);
        reserva.setSituacao(true);
        reserva.setQtdHospedes(2);

        var response = ReservaResponse.fromEntity(reserva);

        assertNotNull(response, "Response não deve ser nulo");

        assertEquals(reserva.getId(), response.getId(), "ID da reserva deve ser igual");
        assertEquals(reserva.getCheckin(), response.getCheckin(), "Data de check-in deve ser igual");
        assertEquals(reserva.getCheckout(), response.getCheckout(), "Data de check-out deve ser igual");
        assertEquals(reserva.getSituacao(), response.getSituacao(), "Situação deve ser igual");
        assertEquals(reserva.getQtdHospedes(), response.getQtdHospedes(), "Quantidade de hóspedes deve ser igual");

        assertNotNull(response.getHospede(), "Hóspede não deve ser nulo");
        assertEquals(hospede, response.getHospede(), "Hóspede deve ser a mesma referência");
        assertEquals(hospede.getId(), response.getHospede().getId(), "ID do hóspede deve ser igual");
        assertEquals(hospede.getNome(), response.getHospede().getNome(), "Nome do hóspede deve ser igual");

        assertNotNull(response.getQuarto(), "Quarto não deve ser nulo");

        assertEquals(quartoOriginal.getId(), response.getQuarto().getId(), "ID do quarto deve ser igual");
        assertEquals(quartoOriginal.getNumero(), response.getQuarto().getNumero(), "Número do quarto deve ser igual");
        assertEquals(quartoOriginal.getQtdHospedes(), response.getQuarto().getQtdHospedes(), "Quantidade de hóspedes do quarto deve ser igual");
        assertEquals(quartoOriginal.getTipoQuarto(), response.getQuarto().getTipoQuarto(), "Tipo do quarto deve ser igual");
        assertEquals(quartoOriginal.getValor(), response.getQuarto().getValor(), "Valor do quarto deve ser igual");

        assertNotEquals(quartoOriginal.getDisponibilidade(), response.getQuarto().getDisponibilidade(),
                "Campo disponível não deve ter sido copiado");
    }

    @Test
    void deveConverterReserva_comSituacaoFalse() {
        var hospede = new Hospede();
        hospede.setId(2);
        hospede.setNome("Maria Santos");

        var tipoQuarto = new Quarto();
        tipoQuarto.setId(2);
        tipoQuarto.setTipoQuarto(ETipoQuarto.DOUBLE);

        Quarto quarto = new Quarto();
        quarto.setId(20);
        quarto.setNumero(202);
        quarto.setQtdHospedes(1);
        quarto.setTipoQuarto(quarto.getTipoQuarto());
        quarto.setValor(BigDecimal.valueOf(150.00));

        Reservas reserva = new Reservas();
        reserva.setId(200);
        reserva.setCheckin(LocalDate.of(2024, 11, 10));
        reserva.setCheckout(LocalDate.of(2024, 11, 12));
        reserva.setHospede(hospede);
        reserva.setQuarto(quarto);
        reserva.setSituacao(false);
        reserva.setQtdHospedes(1);

        var response = ReservaResponse.fromEntity(reserva);

        assertNotNull(response);
        assertEquals(false, response.getSituacao(), "Situação false deve ser mantida");
        assertEquals(reserva.getId(), response.getId());
        assertEquals(reserva.getCheckin(), response.getCheckin());
        assertEquals(reserva.getCheckout(), response.getCheckout());
    }
}
