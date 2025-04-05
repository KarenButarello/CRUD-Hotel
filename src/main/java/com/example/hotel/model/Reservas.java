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
@Table(name = "reservas")
public class Reservas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "checkin", nullable = false)
    private LocalDate checkin;

    @Column(name = "checkout", nullable = false)
    private LocalDate checkout;

    @ManyToOne
    @JoinColumn(name = "hospede_id", nullable = false)
    private Hospede hospede;

    @ManyToOne
    @JoinColumn( name = "quarto_id", nullable = false)
    private Quarto quarto;

    @Column(name = "situacao")
    private Boolean situacao;
}
