package com.example.hotel.controller;

import com.example.hotel.dto.CancelamentoResponse;
import com.example.hotel.dto.ReservaRequest;
import com.example.hotel.dto.ReservaResponse;
import com.example.hotel.enums.ETipoQuarto;
import com.example.hotel.exception.ValidacaoException;
import com.example.hotel.model.Hospede;
import com.example.hotel.model.Quarto;
import com.example.hotel.model.Reservas;
import com.example.hotel.repository.QuartoRepository;
import com.example.hotel.repository.ReservaRepository;
import com.example.hotel.service.ReservaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaRepository repository;

    @MockitoBean
    private QuartoRepository quartoRepository;

    @MockitoBean
    private ReservaService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Quarto> quartoCaptor;

    @Captor
    private ArgumentCaptor<Reservas> reservaCaptor;

    @Test
    public void listarReservas_deveRetornar200_quandoHouveremReservas() throws Exception {
        var hospede1 = new Hospede();
        hospede1.setId(1);
        hospede1.setNome("Maria");
        hospede1.setCpf("123.456.789-10");
        hospede1.setTelefone("(43)9865-5589");

        var hospede2 = new Hospede();
        hospede2.setId(2);
        hospede2.setNome("Carlos");
        hospede2.setCpf("987.584.789-10");
        hospede2.setTelefone("(43)9988-7766");

        var quarto1 = new Quarto();
        quarto1.setId(1);
        quarto1.setTipoQuarto(ETipoQuarto.SINGLE);

        var quarto2 = new Quarto();
        quarto2.setId(1);
        quarto2.setTipoQuarto(ETipoQuarto.TRIPLE);

        var reserva1 = new ReservaResponse();
        reserva1.setId(1);
        reserva1.setCheckin(LocalDate.of(2025,10,26));
        reserva1.setCheckout(LocalDate.of(2025, 10, 30));
        reserva1.setHospede(hospede1);
        reserva1.setQuarto(quarto1);
        reserva1.setSituacao(true);
        reserva1.setQtdHospedes(1);

        var reserva2 = new ReservaResponse();
        reserva2.setId(2);
        reserva2.setCheckin(LocalDate.of(2025,10,26));
        reserva2.setCheckout(LocalDate.of(2025, 10, 30));
        reserva2.setHospede(hospede2);
        reserva2.setQuarto(quarto2);
        reserva2.setSituacao(true);
        reserva2.setQtdHospedes(3);

        List<ReservaResponse> reservas = Arrays.asList(reserva1, reserva2);

        when(service.listarReservas()).thenReturn(reservas);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].checkin").value("2025-10-26"))
                .andExpect(jsonPath("$[0].checkout").value("2025-10-30"))
                .andExpect(jsonPath("$[0].hospede.nome").value("Maria"))
                .andExpect(jsonPath("$[0].hospede.cpf").value("123.456.789-10"))
                .andExpect(jsonPath("$[0].quarto.tipoQuarto").value("SINGLE"))
                .andExpect(jsonPath("$[0].situacao").value(true))
                .andExpect(jsonPath("$[0].qtdHospedes").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].checkin").value("2025-10-26"))
                .andExpect(jsonPath("$[1].checkout").value("2025-10-30"))
                .andExpect(jsonPath("$[1].hospede.nome").value("Carlos"))
                .andExpect(jsonPath("$[1].hospede.cpf").value("987.584.789-10"))
                .andExpect(jsonPath("$[1].quarto.tipoQuarto").value("TRIPLE"))
                .andExpect(jsonPath("$[1].situacao").value(true))
                .andExpect(jsonPath("$[1].qtdHospedes").value(3));

        verify(service, times(1)).listarReservas();
    }

    @Test
    public void listarReservas_deveRetornar404_quandoNaoHouveremReservas() throws Exception {
        when(service.listarReservas()).thenThrow(new NoSuchElementException("Nenhuma reserva encontrada no sistema."));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhuma reserva encontrada no sistema."));

        verify(service, times(1)).listarReservas();
    }

    @Test
    public void buscarReservaPorId_deveRetornar200_quandoEncontrarReservaPeloId() throws Exception {
        var hospede = new Hospede();
        hospede.setId(1);
        hospede.setNome("Maria");
        hospede.setCpf("123.456.789-10");
        hospede.setTelefone("(43)9865-5589");

        var quarto = new Quarto();
        quarto.setId(1);
        quarto.setTipoQuarto(ETipoQuarto.SINGLE);

        var reserva = new ReservaResponse();
        reserva.setId(1);
        reserva.setCheckin(LocalDate.of(2025,10,26));
        reserva.setCheckout(LocalDate.of(2025, 10, 30));
        reserva.setHospede(hospede);
        reserva.setQuarto(quarto);
        reserva.setSituacao(true);
        reserva.setQtdHospedes(1);

        when(service.buscarReservaPorId(1)).thenReturn(reserva);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service, times(1)).buscarReservaPorId(1);
    }

    @Test
    public void buscarReservaPorId_deveRetornar404_quandoNaoEncontrarReservaPeloId() throws Exception {
        when(service.buscarReservaPorId(1))
                .thenThrow(new NoSuchElementException("Reserva não encontrada"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Reserva não encontrada"));

        verify(service, times(1)).buscarReservaPorId(1);
    }

    @Test
    public void cadastrarReserva_deveRetornar200_quandoReservaCadastradaComSucesso() throws Exception {
        var hospede = new Hospede();
        hospede.setId(1);
        hospede.setNome("Maria");
        hospede.setCpf("123.456.789-10");
        hospede.setTelefone("(43)9865-5589");

        var quarto = new Quarto();
        quarto.setId(1);
        quarto.setTipoQuarto(ETipoQuarto.SINGLE);

        var reserva = new ReservaRequest();
        reserva.setCheckin(LocalDate.of(2025, 7,11));
        reserva.setCheckout(LocalDate.of(2025,7,13));
        reserva.setHospedeId(1);
        reserva.setQuartoId(1);
        reserva.setQtdHospedes(1);

        var reservaEsperada = new ReservaResponse();
        reservaEsperada.setId(1);
        reservaEsperada.setCheckin(LocalDate.of(2025, 7,11));
        reservaEsperada.setCheckout(LocalDate.of(2025,7,13));
        reservaEsperada.setQtdHospedes(1);
        reservaEsperada.setSituacao(true);
        reservaEsperada.setHospede(hospede);
        reservaEsperada.setQuarto(quarto);

        when(service.cadastrarReserva(any(ReservaRequest.class)))
                .thenReturn(reservaEsperada);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.checkin").value(reserva.getCheckin().toString()))
                .andExpect(jsonPath("$.checkout").value(reserva.getCheckout().toString()))
                .andExpect(jsonPath("$.qtdHospedes").value(1))
                .andExpect(jsonPath("$.situacao").value(true))
                .andExpect(jsonPath("$.hospede.id").value(hospede.getId()))
                .andExpect(jsonPath("$.quarto.id").value(quarto.getId()));

        verify(service, times(1)).cadastrarReserva(any(ReservaRequest.class));
    }

    @Test
    void cadastrarReserva_deveRetornarErro400_QuandoRequestForInvalido() throws Exception {
        var reserva = new ReservaRequest();
        reserva.setCheckin(null);
        reserva.setCheckout(LocalDate.of(2025,7,13));
        reserva.setHospedeId(1);
        reserva.setQuartoId(1);
        reserva.setQtdHospedes(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).cadastrarReserva(any(ReservaRequest.class));
    }

    @Test
    public void atualizarReserva_deveRetornar200_quandoReservaAtualizadaComSucesso() throws Exception {
        var hospede = new Hospede();
        hospede.setId(1);
        hospede.setNome("Maria");
        hospede.setDataNascimento(LocalDate.of(1995, 10, 26));
        hospede.setCpf("123.456.789-10");
        hospede.setTelefone("(43)9865-5589");

        var quarto = new Quarto();
        quarto.setId(1);
        quarto.setNumero(102);
        quarto.setTipoQuarto(ETipoQuarto.SINGLE);
        quarto.setValor(BigDecimal.valueOf(80.00));
        quarto.setQtdHospedes(1);
        quarto.setDisponibilidade(true);

        var reservaAtualizada = new ReservaRequest();
        reservaAtualizada.setCheckin(LocalDate.of(2025, 7, 11));
        reservaAtualizada.setCheckout(LocalDate.of(2025, 7, 13));
        reservaAtualizada.setHospedeId(1);
        reservaAtualizada.setQuartoId(1);
        reservaAtualizada.setQtdHospedes(1);

        var response = new ReservaResponse();
        response.setId(1);
        response.setCheckin(LocalDate.of(2025, 7, 11));
        response.setCheckout(LocalDate.of(2025, 7, 13));
        response.setHospede(hospede);
        response.setQuarto(quarto);
        response.setQtdHospedes(1);

        when(service.atualizarReserva(1, reservaAtualizada)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservas/1/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.checkin").value("2025-07-11"))
                .andExpect(jsonPath("$.checkout").value("2025-07-13"))
                .andExpect(jsonPath("$.hospede.id").value(1))
                .andExpect(jsonPath("$.quarto.id").value(1));

        verify(service, times(1)).atualizarReserva(1, reservaAtualizada);
    }

    @Test
    public void atualizarReserva_deveRetornar400_quandoCheckinAposCheckout() throws Exception {
        var reservaInvalida = new ReservaRequest();
        reservaInvalida.setCheckin(LocalDate.of(2025, 7, 15));
        reservaInvalida.setCheckout(LocalDate.of(2025, 7, 13));
        reservaInvalida.setHospedeId(1);
        reservaInvalida.setQuartoId(2);
        reservaInvalida.setQtdHospedes(1);

        doThrow(new ValidacaoException("A data de checkin não pode ser depois da data de checkout"))
                .when(service).atualizarReserva(1, reservaInvalida);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservas/1/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("A data de checkin não pode ser depois da data de checkout"));

        verify(service, times(1)).atualizarReserva(1, reservaInvalida);
    }

    @Test
    public void atualizarReserva_deveRetornar400_quandoQuartoIndisponivel() throws Exception {
        var reserva = new ReservaRequest();
        reserva.setCheckin(LocalDate.of(2025, 7, 11));
        reserva.setCheckout(LocalDate.of(2025, 7, 13));
        reserva.setHospedeId(1);
        reserva.setQuartoId(2);
        reserva.setQtdHospedes(1);

        doThrow(new ValidacaoException("Quarto não disponível para o período selecionado"))
                .when(service).atualizarReserva(1, reserva);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservas/1/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).atualizarReserva(1, reserva);
    }

    @Test
    public void atualizarReserva_deveRetornar404_quandoQuartoNaoEncontrado() throws Exception {
        var reserva = new ReservaRequest();
        reserva.setCheckin(LocalDate.of(2025, 7, 11));
        reserva.setCheckout(LocalDate.of(2025, 7, 13));
        reserva.setHospedeId(1);
        reserva.setQuartoId(1);
        reserva.setQtdHospedes(1);

        doThrow(new NoSuchElementException("Quarto não encontrado"))
                .when(service).atualizarReserva(1, reserva);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservas/1/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isNotFound());

        verify(service, times(1)).atualizarReserva(1, reserva);
    }

    @Test
    public void cancelarReserva_deveRetornar200_quandoReservaCanceladaComSucesso() throws Exception {
        var reservaCancelada = new Reservas();
        reservaCancelada.setId(1);
        reservaCancelada.setSituacao(true);

        var cancelamentoResponse = new CancelamentoResponse(reservaCancelada);

        when(service.cancelarReserva(1))
                .thenReturn(ResponseEntity.ok(cancelamentoResponse));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservas/1/cancelar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservaId").value(1))
                .andExpect(jsonPath("$.dataCancelamento").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.cancelamentoSucesso").value(true));

        verify(service, times(1)).cancelarReserva(1);
    }

    @Test
    public void cancelarReserva_deveRetornar404_quandoNaoEncontrarReserva() throws Exception {
        doThrow(new NoSuchElementException("Reserva não encontrada"))
                .when(service).cancelarReserva(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservas/1/cancelar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).cancelarReserva(1);
    }

    @Test
    public void cancelarReserva_deveRetornar400_quandoReservaJaCancelada() throws Exception {
        doThrow(new ValidacaoException("Reserva já está cancelada"))
                .when(service).cancelarReserva(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservas/1/cancelar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).cancelarReserva(1);
    }

    @Test
    public void cancelarReserva_deveRetornar400_quandoTentativaDeCancelarReservaPassada() throws Exception {
        doThrow(new ValidacaoException("Não é possível cancelar reservas já finalizadas"))
                .when(service).cancelarReserva(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservas/1/cancelar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).cancelarReserva(1);
    }

}
