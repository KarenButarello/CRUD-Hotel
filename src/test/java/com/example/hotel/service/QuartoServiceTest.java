package com.example.hotel.service;

import com.example.hotel.exception.DisponibilidadeException;
import com.example.hotel.exception.NotFoundException;
import com.example.hotel.model.Quarto;
import com.example.hotel.repository.QuartoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
public class QuartoServiceTest {

    @Mock
    private QuartoRepository repository;

    @InjectMocks
    private QuartoService service;

    @Test
    public void buscarTodos_deveRetornarTodosOsQuartos_quandoSolicitado() {
        var quarto1 = new Quarto();
        quarto1.setId(1);

        var quarto2 = new Quarto();
        quarto2.setId(2);

        var quarto3 = new Quarto();
        quarto3.setId(3);

        List<Quarto> quartos = Arrays.asList(quarto1, quarto2, quarto3);

        when(repository.findAll()).thenReturn(quartos);

        List<Quarto> resultado = service.buscarTodos();

        assertEquals(3, resultado.size());
        assertEquals(quartos, resultado);

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void buscarTodos_deveRetornarListaVazia_quandoSolicitado() {
        List<Quarto> listaVazia = Collections.emptyList();

        when(repository.findAll()).thenReturn(listaVazia);

        List<Quarto> listaEsperada = service.buscarTodos();

        assertEquals(0, listaEsperada.size());
        assertEquals(listaVazia, listaEsperada);

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void buscarQuartoPorId_deveRetornarQuarto_quandoInformadoOId() {
        var quarto = new Quarto();
        quarto.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(quarto));

        var resultado = service.buscarQuartoPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());

        verify(repository, times(1)).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void buscarQuartoPorId_deveLancarException_quandoQuartoNaoEncontrado() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarQuartoPorId(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Quarto não encontrado");

        verify(repository, times(1)).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void obterQuartosDisponiveis_deveRetornarQuartosDisponiveis_quandoSolicitado() {
        var quarto1 = new Quarto();
        quarto1.setDisponibilidade(true);

        var quarto2 = new Quarto();
        quarto2.setDisponibilidade(true);

        var quarto3 = new Quarto();
        quarto3.setDisponibilidade(false);

        List<Quarto> quartosDisponiveis = Arrays.asList(quarto1, quarto2);

        when(repository.findByDisponibilidade(true)).thenReturn(quartosDisponiveis);

        var resultado = service.obterQuartosDisponiveis();

        assertEquals(quartosDisponiveis, resultado);

        verify(repository, times(1)).findByDisponibilidade(true);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void obterQuartosDisponivies_deveLancarException_quandoNaoHouverQuartosDisponiveis() {
        List<Quarto> quartos = Collections.emptyList();

        when(repository.findByDisponibilidade(true)).thenReturn(quartos);

        assertThatThrownBy(() -> service.obterQuartosDisponiveis())
                .isInstanceOf(DisponibilidadeException.class)
                .hasMessage("Nenhum quarto disponível no momento");

        verify(repository, times(1)).findByDisponibilidade(true);
        verifyNoMoreInteractions(repository);
    }
}
