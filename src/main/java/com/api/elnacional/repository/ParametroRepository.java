package com.api.elnacional.repository;

import com.api.elnacional.domain.Parametro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametroRepository extends JpaRepository<Parametro, Integer> {

    @Query("SELECT p.valor FROM Parametro p WHERE :nombre is null or p.nombre = :nombre")
    public String findValorByNombre(String nombre);
}
