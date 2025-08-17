package com.aluracursos.LiterAlura.repository;

import com.aluracursos.LiterAlura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombreAndFechaNacimientoAndFechaFallecimiento(
            String nombre,
            Integer fechaNacimiento,
            Integer fechaFallecimiento
    );

    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento <= :year AND (a.fechaFallecimiento IS NULL OR a.fechaFallecimiento >= :year)")
    List<Autor> buscarAutoresVivosEnAnio(@Param("year") Integer year);
}