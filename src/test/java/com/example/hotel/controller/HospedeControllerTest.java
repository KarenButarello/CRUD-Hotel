package com.example.hotel.controller;

import com.example.hotel.dto.HospedeRequest;
import com.example.hotel.exception.ValidacaoException;
import com.example.hotel.model.Hospede;
import com.example.hotel.service.HospedeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HospedeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospedeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void buscarHospede_deveRetornar200_quandoIdValido() throws Exception {
        var hospede = new Hospede();
        hospede.setId(1);
        hospede.setNome("Jose");

        when(service.buscarHospedePorId(ArgumentMatchers.any()))
                .thenReturn(hospede);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Jose"));

        verify(service, times(1)).buscarHospedePorId(1);
    }

    @Test
    public void buscarHospede_deveRetornar404_quandoNaoEncontrarHospede() throws Exception {
        var id = 1;
        when(service.buscarHospedePorId(ArgumentMatchers.any()))
                .thenThrow(new EntityNotFoundException("Hóspede não encontrado com o ID: " + id));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(service, times(1)).buscarHospedePorId(1);
    }

    @Test
    public void buscarHospede_deveRetornar500_quandoErroInterno() throws Exception {
        when(service.buscarHospedePorId(ArgumentMatchers.any()))
                .thenThrow(new RuntimeException("Erro interno do servidor"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(service, times(1)).buscarHospedePorId(1);
    }

    @Test
    public void listarTodos_deveRetornar200_quandoHouverListaDeHospedes() throws Exception {
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

        List<Hospede> hospedes = Arrays.asList(hospede1, hospede2);

        when(service.listarTodos()).thenReturn(hospedes);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hospede")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Maria"))
                .andExpect(jsonPath("$[0].cpf").value("123.456.789-10"))
                .andExpect(jsonPath("$[0].telefone").value("(43)9865-5589"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Carlos"))
                .andExpect(jsonPath("$[1].cpf").value("987.584.789-10"))
                .andExpect(jsonPath("$[1].telefone").value("(43)9988-7766"));

        verify(service, times(1)).listarTodos();
    }

    @Test
    public void listarTodos_deveRetornar404_quandoNaoHouverListaDeHospedes() throws Exception {
       List<Hospede> hospedes = Collections.emptyList();

       when(service.listarTodos()).thenReturn(hospedes);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hospede")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

       verify(service, times(1)).listarTodos();
    }

    @Test
    public void salvarHospede_deveRetornar200_quandoForRegistrarNovoHospede() throws Exception {
       var request = new HospedeRequest();
       request.setNome("Karen");
       request.setCpf("123.456.789-10");
       request.setTelefone("(43)3322-7533");
       request.setDataNascimento(LocalDate.of(1995, 10, 26));

       var hospede = new Hospede();
       hospede.setId(1);
       hospede.setNome("Karen");
       hospede.setCpf("123.456.789-10");
       hospede.setTelefone("(43)3322-7533");
       hospede.setDataNascimento(LocalDate.of(1995, 10, 26));

       when(service.salvarHospede(request)).thenReturn(hospede);

       mockMvc.perform(MockMvcRequestBuilders.post("/api/hospede")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.nome").value("Karen"))
               .andExpect(jsonPath("$.dataNascimento").value("26/10/1995"))
               .andExpect(jsonPath("$.telefone").value("(43)3322-7533"))
               .andExpect(jsonPath("$.cpf").value("123.456.789-10"));

       verify(service, times(1)).salvarHospede(any(HospedeRequest.class));
    }

    @Test
    public void salvarHospede_deveRetornar400_quandoHospedeJaCadastrado() throws Exception {
        var request = new HospedeRequest();
        request.setNome("Karen");
        request.setCpf("123.456.789-10");
        request.setTelefone("(43)3322-7533");
        request.setDataNascimento(LocalDate.of(1995, 10, 26));

        when(service.salvarHospede(request))
                .thenThrow(new ValidacaoException("CPF 123.456.789-10 já está cadastrado no sistema."));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/hospede")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).salvarHospede(any(HospedeRequest.class));
    }

    @Test
    public void atualizarHospede_deveRetornar200_quandoAtualizarDadosDoHospede() throws Exception {
        var hospede = new Hospede();
        hospede.setId(1);
        hospede.setNome("Karen Keiko");
        hospede.setCpf("123.456.789-10");
        hospede.setTelefone("(43)3322-7533");
        hospede.setDataNascimento(LocalDate.of(1995, 10, 26));

        var atualizado = new Hospede();
        atualizado.setId(1);
        atualizado.setNome("Karen Keiko");
        atualizado.setCpf("123.456.789-10");
        atualizado.setTelefone("(43)3322-7533");
        atualizado.setDataNascimento(LocalDate.of(1995, 10, 26));

        when(service.atualizarDadosHospede(1, hospede)).thenReturn(atualizado);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospede)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Karen Keiko"))
                .andExpect(jsonPath("$.dataNascimento").value("26/10/1995"))
                .andExpect(jsonPath("$.telefone").value("(43)3322-7533"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-10"));

        verify(service, times(1)).atualizarDadosHospede(1, atualizado);
    }

    @Test
    public void atualizarHospede_deveRetornar400_quandoNomeNull() throws Exception {
        var hospedeInvalido = new Hospede();
        hospedeInvalido.setId(1);
        hospedeInvalido.setNome(null);
        hospedeInvalido.setCpf("123.456.789-10");
        hospedeInvalido.setTelefone("(43)3322-7533");
        hospedeInvalido.setDataNascimento(LocalDate.of(1995, 10, 26));

        when(service.atualizarDadosHospede(eq(1), any(Hospede.class)))
                .thenThrow(new ValidacaoException("Nome é obrigatório"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospedeInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nome é obrigatório"));

        verify(service, times(1)).atualizarDadosHospede(eq(1), any(Hospede.class));
    }

    @Test
    public void atualizarHospede_deveRetornar400_quandoCpfInvalido() throws Exception {
        var hospedeInvalido = new Hospede();
        hospedeInvalido.setId(1);
        hospedeInvalido.setNome("Karen");
        hospedeInvalido.setCpf("123");
        hospedeInvalido.setTelefone("(43)3322-7533");
        hospedeInvalido.setDataNascimento(LocalDate.of(1995, 10, 26));

        when(service.atualizarDadosHospede(eq(1), any(Hospede.class)))
                .thenThrow(new ValidacaoException("CPF deve estar no formato XXX.XXX.XXX-XX"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospedeInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF deve estar no formato XXX.XXX.XXX-XX"));

        verify(service, times(1)).atualizarDadosHospede(eq(1), any(Hospede.class));
    }

    @Test
    public void atualizarHospede_deveRetornar400_quandoDataDeNascimentoFutura() throws Exception {
        var hospedeInvalido = new Hospede();
        hospedeInvalido.setId(1);
        hospedeInvalido.setNome("Karen");
        hospedeInvalido.setCpf("123");
        hospedeInvalido.setTelefone("(43)3322-7533");
        hospedeInvalido.setDataNascimento(LocalDate.of(2030, 10, 26));

        when(service.atualizarDadosHospede(eq(1), any(Hospede.class)))
                .thenThrow(new ValidacaoException("A data de nascimento não pode ser maior que a atual"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospedeInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("A data de nascimento não pode ser maior que a atual"));

        verify(service, times(1)).atualizarDadosHospede(eq(1), any(Hospede.class));
    }

    @Test
    public void atualizarHospede_deveRetornar400_quandoDataDeNascimentoNull() throws Exception {
        var hospedeInvalido = new Hospede();
        hospedeInvalido.setId(1);
        hospedeInvalido.setNome("Karen");
        hospedeInvalido.setCpf("123");
        hospedeInvalido.setTelefone("(43)3322-7533");
        hospedeInvalido.setDataNascimento(null);

        when(service.atualizarDadosHospede(eq(1), any(Hospede.class)))
                .thenThrow(new ValidacaoException("A data de nascimento é obrigatória."));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospedeInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("A data de nascimento é obrigatória."));

        verify(service, times(1)).atualizarDadosHospede(eq(1), any(Hospede.class));
    }

    @Test
    public void atualizarHospede_deveRetornar400_quandoTelefoneNull() throws Exception {
        var hospedeInvalido = new Hospede();
        hospedeInvalido.setId(1);
        hospedeInvalido.setNome("Karen");
        hospedeInvalido.setCpf("123");
        hospedeInvalido.setTelefone(null);
        hospedeInvalido.setDataNascimento(LocalDate.of(2030, 10, 26));

        when(service.atualizarDadosHospede(eq(1), any(Hospede.class)))
                .thenThrow(new ValidacaoException("O telefone é obrigatório."));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospedeInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("O telefone é obrigatório."));

        verify(service, times(1)).atualizarDadosHospede(eq(1), any(Hospede.class));
    }

    @Test
    public void deletarHospede_deveRetornar200_quandoDeletarHospede() throws Exception {
        var hospede = new Hospede();
        hospede.setId(1);
        hospede.setNome("Karen Keiko");
        hospede.setCpf("123.456.789-10");
        hospede.setTelefone("(43)3322-7533");
        hospede.setDataNascimento(LocalDate.of(1995, 10, 26));

        doNothing().when(service).deletarHospede(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).deletarHospede(1);
    }

    @Test
    public void deletarHospede_deveRetornar404_quandoHospedeNaoEncontrado() throws Exception {
        var id = 1;
        doThrow(new EntityNotFoundException("Hóspede não encontrado com o ID: " + id))
                .when(service).deletarHospede(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/hospede/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).deletarHospede(id);
    }
}
