package com.example.hotel.model;

import com.example.hotel.enums.ETipoQuarto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Data
@Getter
@Setter
@Table(name = "quarto")
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "qtd_hospedes")
    private Integer qtdHospedes;

    @Column(name = "tipo_quarto")
    @Enumerated(EnumType.STRING)
    private ETipoQuarto tipoQuarto;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "disponibilidade")
    private Boolean disponibilidade;
}
