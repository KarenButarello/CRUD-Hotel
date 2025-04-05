package com.example.hotel.enums;

import lombok.Getter;

@Getter
public enum ETipoQuarto {

    SINGLE ("SINGLE"),
    DOUBLE ("DOUBLE"),
    TRIPLE ("TRIPLE"),
    EXECUTIVO ("EXECUTIVO"),
    FAMILIA ("FAMILIA");

    // acessar a descrição
    private final String descricao;

    // Construtor do enum
    ETipoQuarto(String descricao) {
        this.descricao = descricao;
    }

}
