package com.example.hotel;

import lombok.Getter;

@Getter
public enum ETipoQuarto {

    SINGLE ("Quarto com uma cama de solteiro"),
    DOUBLE ("Quarto com duas camas de solteiro"),
    TRIPLE ("Quarto com três camas de solteiro"),
    EXECUTIVO ("Quarto com uma cama de casal"),
    FAMILIA ("Quarto com uma cama de casal e duas de solteiro");

    // acessar a descrição
    private final String descricao;

    // Construtor do enum
    ETipoQuarto(String descricao) {
        this.descricao = descricao;
    }

}
