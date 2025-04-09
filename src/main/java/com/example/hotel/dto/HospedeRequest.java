package com.example.hotel.dto;

import com.example.hotel.model.Hospede;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospedeRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefone;

    @NotBlank(message = "O CPF é obrigatório.")
    private String cpf;

    public static HospedeRequest of(Hospede hospede) {
        return HospedeRequest.builder()
                .nome(hospede.getNome())
                .dataNascimento(hospede.getDataNascimento())
                .telefone(hospede.getTelefone())
                .cpf(hospede.getCpf())
                .build();
    }
}
