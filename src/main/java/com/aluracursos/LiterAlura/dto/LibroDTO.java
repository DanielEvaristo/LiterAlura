package com.aluracursos.LiterAlura.dto;

import java.util.List;

public record LibroDTO(
        String titulo,
        List<AutorDTO> autores,
        List<String> idiomas,
        Integer cantidadDescargas
) {}