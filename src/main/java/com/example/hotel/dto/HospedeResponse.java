package com.example.hotel.dto;

import com.example.hotel.model.Hospede;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospedeResponse {
    private Integer id;
    private String nome;
    private LocalDate dataNascimento;
    private String telefone;
    private String cpf;
}
