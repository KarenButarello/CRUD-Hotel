package com.example.hotel.dto;

import com.example.hotel.model.Reservas;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CancelamentoResponseTest {

    @Mock
    private Reservas reservas;

    @Test
    void deveCriarCancelamentoResponse_comSucesso() {
        when(reservas.getId()).thenReturn(123);

        var response = new CancelamentoResponse(reservas);

        assertEquals(123, response.getReservaId());
        assertEquals(LocalDate.now(), response.getDataCancelamento());
        assertTrue(response.isCancelamentoSucesso());
    }

    @Test
    void deveDefinirDataCancelamento_comoDataAtual() {
        when(reservas.getId()).thenReturn(456);
        LocalDate dataEsperada = LocalDate.now();

        var response = new CancelamentoResponse(reservas);

        assertEquals(dataEsperada, response.getDataCancelamento());
    }

    @Test
    void deveDefinirCancelamentoSucesso_comoTrue() {
        when(reservas.getId()).thenReturn(1);

        var response = new CancelamentoResponse(reservas);

        assertTrue(response.isCancelamentoSucesso());
    }

    @Test
    void deveExtrairIdDaReserva_comSucesso() {
        when(reservas.getId()).thenReturn(1);

        var response = new CancelamentoResponse(reservas);

        assertEquals(1, response.getReservaId());
        verify(reservas, times(1)).getId();
    }

}
