package com.example.hotel.repository;

import com.example.hotel.model.Quarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuartoRepository extends JpaRepository<Quarto, Integer> {

    List<Quarto> findByDisponibilidade(Boolean disponibilidade);
}