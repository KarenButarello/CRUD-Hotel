package com.example.hotel.dto;

import com.example.hotel.model.Reservas;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CancelamentoResponse {

    private Integer reservaId;
    private LocalDate dataCancelamento;
    private boolean cancelamentoSucesso;


    public CancelamentoResponse(Reservas reserva) {
        this.reservaId = reserva.getId();
        this.dataCancelamento = LocalDate.now();
        this.cancelamentoSucesso = true;
    }
}
