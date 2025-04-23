package com.example.hotel.dto;

import com.example.hotel.model.Reservas;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservaRequest {

    @NotNull(message = "A data de checkin é obrigatória")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate checkin;

    @NotNull(message = "A data de checkout é obrigatória")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate checkout;

    @NotNull(message = "Hospede deve ser preenchido")
    private Integer hospedeId;

    @NotNull(message = "Quarto deve ser preenchido")
    private Integer quartoId;

    public static ReservaRequest of(Reservas reserva) {
        return ReservaRequest.builder()
                .checkin(reserva.getCheckin())
                .checkout(reserva.getCheckout())
                .hospedeId(reserva.getHospede().getId())
                .quartoId(reserva.getQuarto().getId())
                .build();
    }
}
