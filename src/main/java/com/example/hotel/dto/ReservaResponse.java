package com.example.hotel.dto;

import com.example.hotel.model.Hospede;
import com.example.hotel.model.Quarto;
import com.example.hotel.model.Reservas;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservaResponse {

    private Integer id;
    private LocalDate checkin;
    private LocalDate checkout;
    private Hospede hospede;
    private Quarto quarto;
    private Boolean situacao;
    private Integer qtdHospedes;

    public static ReservaResponse fromEntity(Reservas reserva) {
        ReservaResponse dto = new ReservaResponse();
        dto.setId(reserva.getId());
        dto.setCheckin(reserva.getCheckin());
        dto.setCheckout(reserva.getCheckout());
        dto.setHospede(reserva.getHospede());

        // Converter quarto para DTO sem disponibilidade
        Quarto quartoDTO = new Quarto();
        quartoDTO.setId(reserva.getQuarto().getId());
        quartoDTO.setNumero(reserva.getQuarto().getNumero());
        quartoDTO.setQtdHospedes(reserva.getQuarto().getQtdHospedes());
        quartoDTO.setTipoQuarto(reserva.getQuarto().getTipoQuarto());
        quartoDTO.setValor(reserva.getQuarto().getValor());

        dto.setQuarto(quartoDTO);
        dto.setSituacao(reserva.getSituacao());
        dto.setQtdHospedes(reserva.getQtdHospedes());

        return dto;
    }
}