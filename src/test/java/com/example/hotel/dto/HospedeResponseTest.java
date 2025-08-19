package com.example.hotel.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HospedeResponseTest {

    @Test
    void deveCriarHospede_usandoConstrutorPadrao() {
        var response = new HospedeResponse();

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getNome());
        assertNull(response.getCpf());
        assertNull(response.getTelefone());
        assertNull(response.getDataNascimento());
    }

    @Test
    void deveCriarHospede_usandoConstrutorComArgumentos() {
        var response = new HospedeResponse(
                1,
                "Karen",
                LocalDate.of(1995, 10, 26),
                "(43)99999-9999",
                "123.456.789-10");

        var response2 = new HospedeResponse();
        response2.setId(1);
        response2.setNome("Karen");
        response2.setCpf("123.456.789-10");
        response2.setTelefone("(43)99999-9999");
        response2.setDataNascimento(LocalDate.of(1995, 10, 26));

        assertNotNull(response);
        assertEquals(response2.getId(), response.getId());
        assertEquals(response2.getNome(), response.getNome());
        assertEquals(response2.getDataNascimento(), response.getDataNascimento());
        assertEquals(response2.getTelefone(), response.getTelefone());
        assertEquals(response2.getCpf(), response.getCpf());
    }


}
