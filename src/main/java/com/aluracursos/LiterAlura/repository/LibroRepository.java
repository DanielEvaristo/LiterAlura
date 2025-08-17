package com.aluracursos.LiterAlura.repository;

import com.aluracursos.LiterAlura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    @Query("SELECT l FROM Libro l WHERE LOWER(l.idioma) = LOWER(:idioma)")
    List<Libro> buscarLibrosPorIdiomaIgnoreCase(@Param("idioma") String idioma);

    boolean existsByTitulo(String titulo);
}