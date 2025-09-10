package com.example.hotel.repository;

import com.example.hotel.model.Hospede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospedeRepository extends JpaRepository<Hospede, Integer> {

    boolean existsByCpf(String cpf);

    Optional<Hospede> findByNome(String nome);

}
