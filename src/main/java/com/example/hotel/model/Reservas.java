package com.example.hotel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Data
@Table(name = "Reservas")
public class Reservas {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(nullable = false, name = "Checkin")
    private LocalDate checkin;

    @Column(nullable = false, name = "Checkout")
    private LocalDate checkout;

    @ManyToOne
    @JoinColumn(name = "hospede_id", nullable = false)
    private Hospede hospedeId;

    @ManyToOne
    @JoinColumn( name = "Quarto_Id", nullable = false)
    private Quarto quartoId;

    @Column(name = "Situacao")
    private Boolean situacao;
}
