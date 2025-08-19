package com.example.hotel.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HospedeRequestTest {

    @Test
    public void of_deveCriarHospede_comRequestValido() {
        var request = HospedeRequest.builder()
                .nome("Karen")
                .cpf("123.456.789-10")
                .dataNascimento(LocalDate.of(1995, 10, 26))
                .telefone("(43) 9999-9999")
                .build();

        assertEquals("Karen", request.getNome());
        assertEquals("123.456.789-10", request.getCpf());
        assertEquals(LocalDate.of(1995, 10, 26), request.getDataNascimento());
        assertEquals("(43) 9999-9999", request.getTelefone());
    }
}
