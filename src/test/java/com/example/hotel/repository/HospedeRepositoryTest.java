package com.example.hotel.repository;

import com.example.hotel.model.Hospede;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class HospedeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HospedeRepository repository;

    @Test
    void deveRetornarTrue_quandoHouverCpf() {
        var hospede = new Hospede();
        hospede.setNome("Karen");
        hospede.setCpf("10987654321");
        hospede.setDataNascimento(LocalDate.of(1995, 10, 26));
        hospede.setTelefone("(43)99999-9999");

        entityManager.persistAndFlush(hospede);

        var cpfExistente = repository.existsByCpf("10987654321");

        assertThat(cpfExistente).isTrue();
    }

    @Test
    void deveRetornarFalse_quandoNaoEncontrarCpf() {
        var hospede = new Hospede();
        hospede.setNome("Karen");
        hospede.setCpf("12345678910");
        hospede.setDataNascimento(LocalDate.of(1995, 10, 26));
        hospede.setTelefone("(43)99999-9999");

        entityManager.persistAndFlush(hospede);

        var cpfExistente = repository.existsByCpf("10987654321");

        assertThat(cpfExistente).isFalse();
    }
}
