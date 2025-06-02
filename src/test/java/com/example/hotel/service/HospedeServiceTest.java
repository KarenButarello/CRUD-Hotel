package com.example.hotel.service;

import com.example.hotel.dto.HospedeRequest;
import com.example.hotel.exception.ValidacaoException;
import com.example.hotel.model.Hospede;
import com.example.hotel.repository.HospedeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HospedeServiceTest {

    @Mock
    private HospedeRepository repository;

    @InjectMocks
    private HospedeService service;

    @Test
    public void listarTodos_deveRetornarListaDeHospedes_quandoSolicitado() {
        var hospede1 = new Hospede();
        hospede1.setId(1);
        hospede1.setNome("Karen");

        var hospede2 = new Hospede();
        hospede2.setId(2);
        hospede2.setNome("Mateus");

        List<Hospede> hospedes = Arrays.asList(hospede1, hospede2);

        when(repository.findAll()).thenReturn(hospedes);

        List<Hospede> resultado = service.listarTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(hospedes, resultado);

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void listarTodos_deveRetornarListaVazias_quandoNaoHouverHospedesRegistrados() {
        List<Hospede> listaVazia = Collections.emptyList();

        when(repository.findAll()).thenReturn(listaVazia);

        List<Hospede> listaEsperada = service.listarTodos();

        assertNotNull(listaEsperada);
        assertEquals(0, listaEsperada.size());
        assertEquals(listaVazia, listaEsperada);

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void buscarHospedePorId_deveRetornarHospedePeloId_quandoSolicitado() {
        var hospede = new Hospede();
        hospede.setId(1);

        var hospede2 = new Hospede();
        hospede2.setId(2);

        when(repository.findById(1)).thenReturn(Optional.of(hospede));

        var resultado = service.buscarHospedePorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());

        verify(repository, times(1)).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void buscarHospedePorId_deveLancarException_quandoNaoEncontarHospede() {
        when(repository.findById(3)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarHospedePorId(3))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Hóspede não encontrado com o ID: 3" );

        verify(repository, times(1)).findById(3);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void deletarHospede_deveDeletarHospede_quandoSolicitado() {
        var usuario = new Hospede();
        usuario.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(usuario));

        service.deletarHospede(1);

        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).deleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void deletarHospede_deveLancarException_quandoHospedeNaoEncontrado() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletarHospede(1))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Hóspede não encontrado com o ID: " + 1);

        verify(repository, times(1)).findById(1);
        verify(repository, never()).deleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void salvarHospede_deveSalvarHospede_quandoSolicitado() {
        var request = new HospedeRequest();
        request.setNome("Karen");
        request.setCpf("123.456.789-10");
        request.setTelefone("(43)3322-7533");
        request.setDataNascimento(LocalDate.of(1995,10,26));

        var hospedeEsperado = new Hospede();
        hospedeEsperado.setId(1);
        hospedeEsperado.setNome("Karen");
        hospedeEsperado.setDataNascimento(LocalDate.of(1995,10, 26));
        hospedeEsperado.setTelefone("(43)3322-7533");
        hospedeEsperado.setCpf("123.456.789-10");

        when(repository.existsByCpf("123.456.789-10")).thenReturn(false);
        when(repository.save(any(Hospede.class))).thenReturn(hospedeEsperado);

        var resultado = service.salvarHospede(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Karen");
        assertThat(resultado.getDataNascimento()).isEqualTo(LocalDate.of(1995,10, 26));
        assertThat(resultado.getTelefone()).isEqualTo("(43)3322-7533");
        assertThat(resultado.getCpf()).isEqualTo("123.456.789-10");

        verify(repository, times(1)).existsByCpf("123.456.789-10");
        verify(repository, times(1)).save(any(Hospede.class));
    }

    @Test
    public void salvarHospede_deveLancarException_quandoCpfJaCadastrado() {
        var request = new HospedeRequest();
        request.setCpf("123.456.789-10");

        when(repository.existsByCpf("123.456.789-10")).thenReturn(true);

        assertThatThrownBy(() -> service.salvarHospede(request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("CPF " + request.getCpf() + " já está cadastrado no sistema.");

        verify(repository, times(1)).existsByCpf("123.456.789-10");
        verify(repository, never()).save(any());
    }

    @Test
    public void atualizarDadosHospede_deveAtualizarDados_quandoSolicitado() {
        var hospedeParaAtualizar = new Hospede();
        hospedeParaAtualizar.setNome("Karen Novo");

        var hospedeEsperado = new Hospede();
        hospedeEsperado.setId(1);
        hospedeEsperado.setNome("Karen Novo");

        when(repository.findById(1)).thenReturn(Optional.of(hospedeEsperado));
        when(repository.save(any(Hospede.class))).thenReturn(hospedeEsperado);

        var resultado = service.atualizarDadosHospede(1, hospedeParaAtualizar);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Karen Novo");

        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).save(resultado);

    }

    @Test
    public void atualizarHospede_deveLancarException_quandoHospedeNaoEncontrado() {
        Hospede hospedeParaAtualizar = new Hospede();
        hospedeParaAtualizar.setNome("João Silva");

        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarDadosHospede(1, hospedeParaAtualizar))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Hóspede não encontrado com o ID: " + 1);

        verify(repository, times(1)).findById(1);
        verify(repository, never()).save(any(Hospede.class));

    }
}
