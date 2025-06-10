package com.example.hotel.service;

import com.example.hotel.dto.CancelamentoResponse;
import com.example.hotel.dto.ReservaRequest;
import com.example.hotel.dto.ReservaResponse;
import com.example.hotel.enums.ETipoQuarto;
import com.example.hotel.exception.DisponibilidadeException;
import com.example.hotel.exception.ValidacaoException;
import com.example.hotel.model.Hospede;
import com.example.hotel.model.Quarto;
import com.example.hotel.model.Reservas;
import com.example.hotel.repository.HospedeRepository;
import com.example.hotel.repository.QuartoRepository;
import com.example.hotel.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ReservaServiceTest {

    @Mock
    private ReservaRepository repository;

    @Mock
    private QuartoRepository quartoRepository;

    @Mock
    private HospedeRepository hospedeRepository;

    @InjectMocks
    private ReservaService service;

    @Test
    public void listarReservas_deveListarReservas_quandoSolicitado() {
        var hospede1 = new Hospede();
        hospede1.setId(1);
        hospede1.setNome("João Silva");
        hospede1.setTelefone("(43)3322-7533");
        hospede1.setCpf("123.456.789-10");
        hospede1.setDataNascimento(LocalDate.of(1995,1,1));

        var hospede2 = new Hospede();
        hospede2.setId(2);
        hospede2.setNome("João Maria");
        hospede2.setTelefone("(43)3322-7533");
        hospede2.setCpf("987.654.111-10");
        hospede2.setDataNascimento(LocalDate.of(1995,9,1));

        var hospede3 = new Hospede();
        hospede3.setId(2);
        hospede3.setNome("Jose Maria");
        hospede3.setTelefone("(43)3322-7533");
        hospede3.setCpf("987.654.111-10");
        hospede3.setDataNascimento(LocalDate.of(1995,9,1));

        var quarto1 = new Quarto();
        quarto1.setId(1);
        quarto1.setNumero(1);
        quarto1.setTipoQuarto(ETipoQuarto.EXECUTIVO);

        var quarto2 = new Quarto();
        quarto2.setId(2);
        quarto2.setNumero(102);
        quarto2.setTipoQuarto(ETipoQuarto.DOUBLE);

        var quarto3 = new Quarto();
        quarto3.setId(3);
        quarto3.setNumero(501);
        quarto3.setTipoQuarto(ETipoQuarto.FAMILIA);

        var reserva1 = new Reservas();
        reserva1.setId(1);
        reserva1.setQuarto(quarto1);
        reserva1.setHospede(hospede1);
        reserva1.setCheckin(LocalDate.now());
        reserva1.setCheckout(LocalDate.now().plusDays(2));

        var reserva2 = new Reservas();
        reserva2.setId(2);
        reserva2.setQuarto(quarto2);
        reserva2.setHospede(hospede2);
        reserva2.setCheckin(LocalDate.now().plusDays(1));
        reserva2.setCheckout(LocalDate.now().plusDays(3));

        var reserva3 = new Reservas();
        reserva3.setId(3);
        reserva3.setQuarto(quarto3);
        reserva3.setHospede(hospede3);
        reserva3.setCheckin(LocalDate.now().plusDays(5));
        reserva3.setCheckout(LocalDate.now().plusDays(7));

        List<Reservas> reservas = Arrays.asList(reserva1, reserva2, reserva3);

        when(repository.findAll()).thenReturn(reservas);

        List<ReservaResponse> resultado = service.listarReservas();

        assertEquals(3, resultado.size());
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void listarReservas_deveRetornarListaVaziaELancarException_quandoNaoHouverReservas() {
        List<Reservas> listaVazia = Collections.emptyList();

        when(repository.findAll()).thenReturn(listaVazia);

        assertThatThrownBy(() -> service.listarReservas())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Nenhuma reserva encontrada no sistema.");

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void buscarReservaPorId_deveRetornarReserva_quandoBuscarPeloId() {
        var hospede1 = new Hospede();
        hospede1.setId(1);
        hospede1.setNome("João Silva");
        hospede1.setTelefone("(43)3322-7533");
        hospede1.setCpf("123.456.789-10");
        hospede1.setDataNascimento(LocalDate.of(1995,1,1));

        var quarto1 = new Quarto();
        quarto1.setId(1);
        quarto1.setNumero(1);
        quarto1.setTipoQuarto(ETipoQuarto.EXECUTIVO);

        var reserva = new Reservas();
        reserva.setId(1);
        reserva.setHospede(hospede1);
        reserva.setQuarto(quarto1);
        reserva.setQtdHospedes(1);
        reserva.setCheckin(LocalDate.now());
        reserva.setCheckout(LocalDate.now().plusDays(2));

        when(repository.findById(1)).thenReturn(Optional.of(reserva));

        var reservaProcurada = service.buscarReservaPorId(1);

        assertNotNull(reservaProcurada);
        assertEquals(1, reservaProcurada.getId());

        verify(repository, times(1)).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void buscarReservaPorId_deveLancarException_quandoNaoEncontrarReservaPeloId() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarReservaPorId(1))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Reserva não encontrada");

        verify(repository, times(1)).findById(1);
    }

    @Test
    public void cadastrarReserva_deveCadastrarReserva_quandoSolicitado() {
        var hospede1 = new Hospede();
        hospede1.setId(1);
        hospede1.setNome("João Silva");
        hospede1.setTelefone("(43)3322-7533");
        hospede1.setCpf("123.456.789-10");
        hospede1.setDataNascimento(LocalDate.of(1995, 1, 1));

        var quarto1 = new Quarto();
        quarto1.setId(1);
        quarto1.setNumero(1);
        quarto1.setQtdHospedes(4);
        quarto1.setDisponibilidade(true);
        quarto1.setTipoQuarto(ETipoQuarto.EXECUTIVO);

        var request = new ReservaRequest();
        request.setCheckin(LocalDate.now().plusDays(1));
        request.setCheckout(LocalDate.now().plusDays(3));
        request.setQtdHospedes(2);
        request.setHospedeId(hospede1.getId());
        request.setQuartoId(quarto1.getId());

        var reservaEsperada = new Reservas();
        reservaEsperada.setId(1);
        reservaEsperada.setCheckin(request.getCheckin());
        reservaEsperada.setCheckout(request.getCheckout());
        reservaEsperada.setQtdHospedes(request.getQtdHospedes());
        reservaEsperada.setHospede(hospede1);
        reservaEsperada.setQuarto(quarto1);
        reservaEsperada.setSituacao(true);

        var responseEsperada = ReservaResponse.fromEntity(reservaEsperada);
        
        when(hospedeRepository.findById(hospede1.getId())).thenReturn(Optional.of(hospede1));
        when(quartoRepository.findById(quarto1.getId())).thenReturn(Optional.of(quarto1));
        when(repository.save(any(Reservas.class))).thenReturn(reservaEsperada);

        var resultado = service.cadastrarReserva(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(responseEsperada.getId());
        assertThat(resultado.getCheckin()).isEqualTo(request.getCheckin());
        assertThat(resultado.getCheckout()).isEqualTo(request.getCheckout());
        assertThat(resultado.getQtdHospedes()).isEqualTo(request.getQtdHospedes());
        assertThat(resultado.getSituacao()).isTrue();

        verify(hospedeRepository).findById(hospede1.getId());
        verify(quartoRepository).findById(quarto1.getId());
        verify(quartoRepository).save(quarto1);
        verify(repository).save(any(Reservas.class));

        assertThat(quarto1.getDisponibilidade()).isFalse();
    }

    @Test
    public void cadastrarReserva_deveLancarException_quandoRequestNulo() {
        var request = new ReservaRequest();

        assertThrows(NoSuchElementException.class, () -> {
            service.cadastrarReserva(request);
        });

        verifyNoInteractions(repository);
        verify(repository, never()).save(any(Reservas.class));
    }

    @Test
    public void cadastrarReserva_deveLancarException_quandoHospedeNaoEncontrado() {
        var request = new ReservaRequest();
        request.setCheckin(LocalDate.now().plusDays(1));
        request.setCheckout(LocalDate.now().plusDays(3));
        request.setQtdHospedes(2);
        request.setHospedeId(1);
        request.setQuartoId(1);

        when(hospedeRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrarReserva(request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Hospede não encontrado");

        verify(hospedeRepository).findById(1);
        verifyNoInteractions(repository);
        verify(repository, never()).save(any(Reservas.class));
    }

    @Test
    public void cadastrarReserva_deveLancarException_quandoQuartoNaoEncontrado() {
        var request = new ReservaRequest();
        request.setCheckin(LocalDate.now().plusDays(1));
        request.setCheckout(LocalDate.now().plusDays(3));
        request.setQtdHospedes(2);
        request.setHospedeId(1);
        request.setQuartoId(1);

        var hospede = new Hospede();
        hospede.setId(1);

        when(hospedeRepository.findById(1)).thenReturn(Optional.of(hospede));
        when(quartoRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrarReserva(request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Quarto não encontrado");

        verify(quartoRepository).findById(1);
        verifyNoInteractions(repository);
        verify(repository, never()).save(any(Reservas.class));
    }

    @Test
    public void cadastrarReserva_deveLancarException_quandoPeriodoInvalido() {
        var request = new ReservaRequest();
        request.setCheckin(LocalDate.now().plusDays(1));
        request.setCheckout(LocalDate.now().minusDays(3));
        request.setQtdHospedes(2);
        request.setHospedeId(1);
        request.setQuartoId(1);

        var hospede = new Hospede();
        hospede.setId(1);

        var quarto = new Quarto();
        quarto.setId(1);

        when(hospedeRepository.findById(1)).thenReturn(Optional.of(hospede));
        when(quartoRepository.findById(1)).thenReturn(Optional.of(quarto));

        assertThatThrownBy(() -> service.cadastrarReserva(request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("A data de checkin não pode ser depois da data de checkout");

        verifyNoInteractions(repository);
        verify(repository, never()).save(any(Reservas.class));
    }

    @Test
    public void cadastrarReserva_deveLancarException_quandoQuartoSelecionadoIndisponivel() {
        var request = new ReservaRequest();
        request.setCheckin(LocalDate.now().plusDays(1));
        request.setCheckout(LocalDate.now().plusDays(3));
        request.setQtdHospedes(2);
        request.setHospedeId(1);
        request.setQuartoId(1);

        var hospede = new Hospede();
        hospede.setId(1);

        var quarto = new Quarto();
        quarto.setId(1);
        quarto.setDisponibilidade(false);

        when(hospedeRepository.findById(1)).thenReturn(Optional.of(hospede));
        when(quartoRepository.findById(1)).thenReturn(Optional.of(quarto));

        assertThatThrownBy(() -> service.cadastrarReserva(request))
                .isInstanceOf(DisponibilidadeException.class)
                .hasMessage("O quarto não está disponível para reserva.");

        verifyNoInteractions(repository);
        verify(repository, never()).save(any(Reservas.class));
    }

    @Test
    public void cadastrarReserva_devaLancarException_quandoQuantidadeDeHospedeSuperiorACapacidadeDoQuarto() {
        var request = new ReservaRequest();
        request.setCheckin(LocalDate.now().plusDays(1));
        request.setCheckout(LocalDate.now().plusDays(3));
        request.setQtdHospedes(5);
        request.setHospedeId(1);
        request.setQuartoId(1);

        var hospede = new Hospede();
        hospede.setId(1);

        var quarto = new Quarto();
        quarto.setId(1);
        quarto.setQtdHospedes(1);

        when(hospedeRepository.findById(1)).thenReturn(Optional.of(hospede));
        when(quartoRepository.findById(1)).thenReturn(Optional.of(quarto));

        assertThatThrownBy(() -> service.cadastrarReserva(request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Quantidade de hospedes superior a capacidade do quarto");

        verifyNoInteractions(repository);
        verify(repository, never()).save(any(Reservas.class));
    }

    @Test
    public void atualizarReserva_deveAtualizarReserva_quandoSolicitado(){
        var request = new ReservaRequest();
        request.setCheckin(LocalDate.now().plusDays(2));
        request.setCheckout(LocalDate.now().plusDays(5));
        request.setQtdHospedes(3);
        request.setQuartoId(2);

        var reservaAtual = new Reservas();
        reservaAtual.setId(1);
        reservaAtual.setCheckin(LocalDate.now().plusDays(1));
        reservaAtual.setCheckout(LocalDate.now().plusDays(3));
        reservaAtual.setQtdHospedes(2);

        var quartoAntigo = new Quarto();
        quartoAntigo.setId(1);
        quartoAntigo.setDisponibilidade(false);
        reservaAtual.setQuarto(quartoAntigo);

        var quartoNovo = new Quarto();
        quartoNovo.setId(2);
        quartoNovo.setDisponibilidade(true);
        quartoNovo.setQtdHospedes(4);

        var reservaAtualizada = new Reservas();
        reservaAtualizada.setId(1);
        reservaAtualizada.setCheckin(request.getCheckin());
        reservaAtualizada.setCheckout(request.getCheckout());
        reservaAtualizada.setQtdHospedes(request.getQtdHospedes());
        reservaAtualizada.setQuarto(quartoNovo);

        when(repository.findById(1)).thenReturn(Optional.of(reservaAtual));
        when(quartoRepository.findById(2)).thenReturn(Optional.of(quartoNovo));
        when(repository.save(any(Reservas.class))).thenReturn(reservaAtualizada);

        var response = service.atualizarReserva(1, request);

        assertThat(response.getCheckin()).isEqualTo(request.getCheckin());
        assertThat(response.getCheckout()).isEqualTo(request.getCheckout());
        assertThat(response.getQtdHospedes()).isEqualTo(request.getQtdHospedes());

        verify(repository).findById(1);
        verify(quartoRepository).findById(2);
        verify(quartoRepository).save(quartoAntigo);
        verify(quartoRepository).save(quartoNovo);
        verify(repository).save(any(Reservas.class));

        assertThat(quartoAntigo.getDisponibilidade()).isTrue();
        assertThat(quartoNovo.getDisponibilidade()).isFalse();
    }

    @Test
    public void atualizarReserva_deveLancarException_quandoQuartoNaoEncontrado() {
        var reservaExistente = new Reservas();
        reservaExistente.setId(1);

        var request = new ReservaRequest();
        request.setQtdHospedes(3);
        request.setQuartoId(20);

        when(repository.findById(1)).thenReturn(Optional.of(reservaExistente));
        when(quartoRepository.findById(20)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarReserva(1, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Quarto não encontrado");

        verify(repository).findById(1);
        verify(quartoRepository).findById(20);
        verify(repository, never()).save(any());
    }

    @Test
    public void atualizarReserva_deveLancarException_quandoQuartoIndisponivel() {
        var reservaExistente = new Reservas();
        reservaExistente.setId(1);

        var request = new ReservaRequest();
        request.setQtdHospedes(3);
        request.setQuartoId(20);

        when(repository.findById(1)).thenReturn(Optional.of(reservaExistente));
        when(quartoRepository.findById(20)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarReserva(1, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Quarto não encontrado");

        verify(repository).findById(1);
        verify(quartoRepository).findById(20);
        verify(repository, never()).save(any());
    }

    @Test
    public void atualizarReserva_deveLancarException_quandoQuantidadeDeHospedesSuperiorACapacidadeDoQuarto() {
        var reservaExistente = new Reservas();
        reservaExistente.setId(1);

        var request = new ReservaRequest();
        request.setCheckin(LocalDate.now().plusDays(1));
        request.setCheckout(LocalDate.now().plusDays(3));
        request.setQtdHospedes(5);
        request.setQuartoId(1);

        var quarto = new Quarto();
        quarto.setId(1);
        quarto.setQtdHospedes(1);

        when(repository.findById(1)).thenReturn(Optional.of(reservaExistente));
        when(quartoRepository.findById(1)).thenReturn(Optional.of(quarto));

        assertThatThrownBy(() -> service.atualizarReserva(1, request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("capacidade");

        verify(repository).findById(1);
        verify(quartoRepository).findById(1);
        verify(repository, never()).save(any());
    }

    @Test
    public void atualizarReserva_deveLancarException_quandoPeriodoInvalido() {
        var request = new ReservaRequest();
        request.setCheckin(LocalDate.now().plusDays(1));
        request.setCheckout(LocalDate.now().minusDays(3));
        request.setQtdHospedes(2);
        request.setHospedeId(1);
        request.setQuartoId(1);

        var hospede = new Hospede();
        hospede.setId(1);

        var quarto = new Quarto();
        quarto.setId(1);

        var reservaExistente = new Reservas();
        reservaExistente.setId(1);
        reservaExistente.setCheckin(LocalDate.now().plusDays(2));
        reservaExistente.setCheckout(LocalDate.now().plusDays(4));

        when(repository.findById(1)).thenReturn(Optional.of(reservaExistente));
        when(hospedeRepository.findById(1)).thenReturn(Optional.of(hospede));
        when(quartoRepository.findById(1)).thenReturn(Optional.of(quarto));

        assertThatThrownBy(() -> service.atualizarReserva(1, request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("A data de checkin não pode ser depois da data de checkout");

        verify(repository, never()).save(any(Reservas.class));
    }

    @Test
    public void cancelarReserva_deveCancelarReserva_quandoSolicitado() {
        var quarto = new Quarto();
        quarto.setId(1);
        quarto.setDisponibilidade(false);

        var reserva = new Reservas();
        reserva.setId(1);
        reserva.setSituacao(true);
        reserva.setCheckout(LocalDate.now().plusDays(5));
        reserva.setQuarto(quarto);

        var reservaCancelada = new Reservas();
        reservaCancelada.setId(1);
        reservaCancelada.setSituacao(false);
        reservaCancelada.setCheckout(LocalDate.now().plusDays(5));
        reservaCancelada.setQuarto(quarto);

        when(repository.findById(1)).thenReturn(Optional.of(reserva));
        when(repository.save(reserva)).thenReturn(reservaCancelada);
        when(quartoRepository.save(quarto)).thenReturn(quarto);

        ResponseEntity<CancelamentoResponse> result = service.cancelarReserva(1);

        assertThat(result.getBody()).isNotNull();

        assertThat(reserva.getSituacao()).isFalse();

        assertThat(quarto.getDisponibilidade()).isTrue();

        verify(repository).findById(1);
        verify(repository).save(reserva);
        verify(quartoRepository).save(quarto);
    }

    @Test
    public void cancelarReserva_deveLancarException_quandoNaoEncontrarReserva() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelarReserva(1))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Reserva não encontrada");

        verify(repository, times(1)).findById(1);
    }

    @Test
    public void cancelarReserva_deveLancarException_quandoReservaJaCancelada() {
        var quarto = new Quarto();
        quarto.setId(1);

        var hospede = new Hospede();
        hospede.setId(1);

        var reserva = new Reservas();
        reserva.setId(1);
        reserva.setSituacao(false);
        reserva.setQuarto(quarto);
        reserva.setHospede(hospede);
        reserva.setCheckin(LocalDate.now().plusDays(1));
        reserva.setCheckout(LocalDate.now().plusDays(3));
        reserva.setQtdHospedes(1);

        when(repository.findById(1)).thenReturn(Optional.of(reserva));
        when(hospedeRepository.findById(1)).thenReturn(Optional.of(hospede));
        when(quartoRepository.findById(1)).thenReturn(Optional.of(quarto));

        assertThatThrownBy(() -> service.cancelarReserva(1))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Reserva já está cancelada");

        verify(repository, never()).save(reserva);
    }

    @Test
    public void cancelarReserva_deveLancarException_quandoTentarCancelarReservasPassadas() {
        var quarto = new Quarto();
        quarto.setId(1);

        var hospede = new Hospede();
        hospede.setId(1);

        var reserva = new Reservas();
        reserva.setId(1);
        reserva.setSituacao(false);
        reserva.setQuarto(quarto);
        reserva.setHospede(hospede);
        reserva.setCheckin(LocalDate.now().minusDays(6));
        reserva.setCheckout(LocalDate.now().minusDays(3));
        reserva.setQtdHospedes(1);

        when(repository.findById(1)).thenReturn(Optional.of(reserva));
        when(hospedeRepository.findById(1)).thenReturn(Optional.of(hospede));
        when(quartoRepository.findById(1)).thenReturn(Optional.of(quarto));

        assertThatThrownBy(() -> service.cancelarReserva(1))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Não é possível cancelar reservas já finalizadas");

        verify(repository, never()).save(reserva);
    }
}
