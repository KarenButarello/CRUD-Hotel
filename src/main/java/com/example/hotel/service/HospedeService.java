package com.example.hotel.service;

import com.example.hotel.dto.HospedeRequest;
import com.example.hotel.exception.ValidacaoException;
import com.example.hotel.model.Hospede;
import com.example.hotel.repository.HospedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HospedeService {

    private final HospedeRepository repository;

    public List<Hospede>listarTodos() {
        return repository.findAll();
    }

    public Hospede buscarHospedePorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Hóspede não encontrado com o ID: " + id));
    }

    public void deletarHospede(Integer id) {
        buscarHospedePorId(id);
        repository.deleteById(id);
    }

    public Hospede atualizarDadosHospede(Integer id, Hospede hospede) {
        var hospedeAtual = buscarHospedePorId(id);

        if (!hospedeAtual.getCpf().equals(hospede.getCpf())) {
            verificarCpfExistente(hospede.getCpf());
            hospedeAtual.setCpf(hospede.getCpf());
        }

        var hospedeRequest = HospedeRequest.of(hospede);

        hospedeAtual.setNome(hospedeRequest.getNome());
        hospedeAtual.setTelefone(hospedeRequest.getTelefone());
        hospedeAtual.setDataNascimento(hospedeRequest.getDataNascimento());

        return repository.save(hospedeAtual);
    }

    public Hospede salvarHospede(HospedeRequest request) {
        verificarCpfExistente(request.getCpf());

        var hospede = new Hospede();
        hospede.setNome(request.getNome());
        hospede.setDataNascimento(request.getDataNascimento());
        hospede.setTelefone(request.getTelefone());
        hospede.setCpf(request.getCpf());

        return repository.save(hospede);
    }

    private void verificarCpfExistente(String cpf) {
        if (repository.existsByCpf(cpf)) {
            throw new ValidacaoException("CPF " + cpf + " já está cadastrado no sistema.");
        }
    }
}
