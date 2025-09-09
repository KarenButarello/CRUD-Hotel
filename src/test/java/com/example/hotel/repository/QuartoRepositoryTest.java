package com.example.hotel.repository;

import com.example.hotel.enums.ETipoQuarto;
import com.example.hotel.model.Quarto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
public class QuartoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuartoRepository repository;

    @Test
    @Sql(statements = "DELETE FROM quarto", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByDisponibilidade_deveRetornarQuartosDisponiveis() {
        var quarto1 = new Quarto();
        quarto1.setNumero(101);
        quarto1.setQtdHospedes(1);
        quarto1.setTipoQuarto(ETipoQuarto.SINGLE);
        quarto1.setValor(BigDecimal.valueOf(120.00));
        quarto1.setDisponibilidade(true);

        var quarto2 = new Quarto();
        quarto2.setNumero(201);
        quarto2.setQtdHospedes(2);
        quarto2.setTipoQuarto(ETipoQuarto.DOUBLE);
        quarto2.setValor(BigDecimal.valueOf(150.00));
        quarto2.setDisponibilidade(true);

        var quarto3 = new Quarto();
        quarto3.setNumero(301);
        quarto3.setQtdHospedes(3);
        quarto3.setTipoQuarto(ETipoQuarto.TRIPLE);
        quarto3.setValor(BigDecimal.valueOf(180.00));
        quarto3.setDisponibilidade(false);

        entityManager.persistAndFlush(quarto1);
        entityManager.persistAndFlush(quarto2);
        entityManager.persistAndFlush(quarto3);
        entityManager.clear();

        List<Quarto> quartosDisponiveis = repository.findByDisponibilidade(true);

        assertThat(quartosDisponiveis).hasSize(2);
        assertThat(quartosDisponiveis)
                .extracting(Quarto::getDisponibilidade)
                .containsOnly(true);
    }

}


