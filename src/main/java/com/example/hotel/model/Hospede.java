package com.example.hotel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
@Table(name = "Hospede")
public class Hospede {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "Nome")
    private String nome;

    @Column(name = "Data_Nascimento")
    private LocalDate dataNascimento;

    @Column(name = "Telefone")
    private String telefone;

    @Column(name = "CPF")
    private String cpf;
}
