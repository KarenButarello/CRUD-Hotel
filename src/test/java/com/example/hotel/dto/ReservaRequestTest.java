package com.example.hotel.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservaRequestTest {

    @Test
    public void of_deveCriarReserva_comRequestValido() {
        var request = ReservaRequest.builder()
                .checkin(LocalDate.now())
                .checkout(LocalDate.now().plusDays(4))
                .hospedeId(1)
                .quartoId(101)
                .qtdHospedes(1)
                .build();

        assertEquals(LocalDate.now(), request.getCheckin());
        assertEquals(LocalDate.now().plusDays(4), request.getCheckout());
        assertEquals(1, request.getHospedeId());
        assertEquals(101, request.getQuartoId());
        assertEquals(1, request.getHospedeId());

    }
}
