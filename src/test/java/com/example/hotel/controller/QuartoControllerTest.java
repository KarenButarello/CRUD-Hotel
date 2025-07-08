package com.example.hotel.controller;

import com.example.hotel.enums.ETipoQuarto;
import com.example.hotel.exception.DisponibilidadeException;
import com.example.hotel.exception.NotFoundException;
import com.example.hotel.model.Quarto;
import com.example.hotel.service.QuartoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class QuartoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuartoService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void buscarQuarto_deveRetornar200_quandoEncontrarListaDeQuartos() throws Exception {
        var quarto1 = new Quarto();
        quarto1.setId(1);
        quarto1.setNumero(101);

        var quarto2 = new Quarto();
        quarto2.setId(2);
        quarto2.setNumero(201);

        List<Quarto> quartos = Arrays.asList(quarto1, quarto2);

        when(service.buscarTodos()).thenReturn(quartos);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/quarto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numero").value(101))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].numero").value(201));

        verify(service, times(1)).buscarTodos();
    }

    @Test
    public void buscarQuarto_deveRetornar404_quandoNaoEncontrarQuartos() throws Exception {
        when(service.buscarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/quarto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).buscarTodos();
    }

    @Test
    public void buscarQuartoPorId_deveRetornar200_quandoIdValido() throws Exception {
        var quarto = new Quarto();
        quarto.setId(1);

        when(service.buscarQuartoPorId(1)).thenReturn(quarto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/quarto/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service, times(1)).buscarQuartoPorId(1);
    }

    @Test
    public void buscarQuartoPorId_deveRetornar404_quandoQuartoNaoEncontrado() throws Exception {
        when(service.buscarQuartoPorId(anyInt()))
                .thenThrow(new NotFoundException("Quarto não encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/quarto/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Quarto não encontrado"))
                .andExpect(status().isNotFound());

        verify(service, times(1)).buscarQuartoPorId(2);

    }

    @Test
    public void obterQuartosDisponiveis_deveRetornar200_quandoHouveremQuartosDisponiveis() throws Exception {
        var quarto1 = new Quarto();
        quarto1.setId(1);
        quarto1.setNumero(101);
        quarto1.setQtdHospedes(1);
        quarto1.setTipoQuarto(ETipoQuarto.SINGLE);
        quarto1.setValor(BigDecimal.valueOf(220.00));
        quarto1.setDisponibilidade(true);

        var quarto2 = new Quarto();
        quarto2.setId(2);
        quarto2.setNumero(201);
        quarto2.setQtdHospedes(2);
        quarto2.setTipoQuarto(ETipoQuarto.DOUBLE);
        quarto2.setValor(BigDecimal.valueOf(220.00));
        quarto2.setDisponibilidade(true);

        List<Quarto> quartosDisponiveis = Arrays.asList(quarto1, quarto2);

        when(service.obterQuartosDisponiveis()).thenReturn(quartosDisponiveis);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/quarto/disponiveis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numero").value(101))
                .andExpect(jsonPath("$[0].qtdHospedes").value(1))
                .andExpect(jsonPath("$[0].tipoQuarto").value("SINGLE"))
                .andExpect(jsonPath("$[0].valor").value(220.00))
                .andExpect(jsonPath("$[0].disponibilidade").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].numero").value(201))
                .andExpect(jsonPath("$[1].qtdHospedes").value(2))
                .andExpect(jsonPath("$[1].tipoQuarto").value("DOUBLE"))
                .andExpect(jsonPath("$[1].valor").value(220.00))
                .andExpect(jsonPath("$[1].disponibilidade").value(true));

        verify(service, times(1)).obterQuartosDisponiveis();
    }

    @Test
    public void obterQuartosDisponiveis_deveRetornar404_quandoNaoHouveremQuartosDisponiveis() throws Exception {
        when(service.obterQuartosDisponiveis()).thenThrow(
                new DisponibilidadeException("Nenhum quarto disponível no momento"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/quarto/disponiveis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).obterQuartosDisponiveis();
    }
}
