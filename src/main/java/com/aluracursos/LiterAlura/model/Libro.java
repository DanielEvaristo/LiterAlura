package com.aluracursos.LiterAlura.model;

import com.aluracursos.LiterAlura.dto.LibroDTO;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String idioma;
    private Integer cantidadDescargas;

    @ManyToOne
    private Autor autor;

    public Libro() {}

    public Libro(String titulo, String idioma, Integer cantidadDescargas, Autor autor) {
        this.titulo = titulo;
        this.idioma = idioma;
        this.cantidadDescargas = cantidadDescargas;
        this.autor = autor;
    }

    public Libro(LibroDTO dto) {
        this.titulo = dto.titulo();
        this.idioma = (dto.idiomas() != null && !dto.idiomas().isEmpty()) ? dto.idiomas().get(0) : "desconocido";
        this.cantidadDescargas = dto.cantidadDescargas();
    }
    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getCantidadDescargas() {
        return cantidadDescargas;
    }

    public void setCantidadDescargas(Integer cantidadDescargas) {
        this.cantidadDescargas = cantidadDescargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "Libro: " + titulo +
                " | Idioma: " + idioma +
                " | Descargas: " + cantidadDescargas +
                " | Autor: " + (autor != null ? autor.getNombre() : "Desconocido");
    }
}
