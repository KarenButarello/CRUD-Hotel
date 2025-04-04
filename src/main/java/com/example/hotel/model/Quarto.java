package com.example.hotel.model;

import com.example.hotel.ETipoQuarto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Data
@Getter
@Setter
@Table(name = "Quarto")
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "Numero")
    private Integer numero;

    @Column(name = "Tipo_Quarto")
    private ETipoQuarto tipo;

    @Column(name = "Valor")
    private BigDecimal valor;

    @Column(name = "Disponibilidade")
    private Boolean disponibilidade;
}
