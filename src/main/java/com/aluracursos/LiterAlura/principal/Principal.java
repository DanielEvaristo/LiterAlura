package com.aluracursos.LiterAlura.principal;

import com.aluracursos.LiterAlura.dto.AutorDTO;
import com.aluracursos.LiterAlura.dto.LibroDTO;
import com.aluracursos.LiterAlura.model.Autor;
import com.aluracursos.LiterAlura.model.Biblioteca;
import com.aluracursos.LiterAlura.model.DatosLibro;
import com.aluracursos.LiterAlura.model.Libro;
import com.aluracursos.LiterAlura.repository.AutorRepository;
import com.aluracursos.LiterAlura.repository.LibroRepository;
import com.aluracursos.LiterAlura.service.ConsumirApi;
import com.aluracursos.LiterAlura.service.Conversor;

import java.util.List;
import java.util.Scanner;

public class Principal {

    private final Scanner sc = new Scanner(System.in);
    private final ConsumirApi api = new ConsumirApi();
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final Conversor conversor = new Conversor();
    private static final String URL_BASE = "https://gutendex.com/books/";

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void mostrarMenu() {
        // Mensaje de bienvenida
        System.out.println("¡Bienvenido a literalura!");

        boolean activo = true;
        while (activo) {
            imprimirOpciones();
            String opcion = sc.nextLine();
            switch (opcion) {
                case "1" -> buscarLibroPorTitulo();
                case "2" -> listarLibros();
                case "3" -> listarAutores();
                case "4" -> autoresVivosPorAnio();
                case "5" -> librosPorIdioma();
                case "0" -> {
                    System.out.println("¡Gracias por usar LiterAlura!");
                    activo = false;
                }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private void imprimirOpciones() {
        System.out.println("Para continuar elija una opción:");
        System.out.println("    1 - Buscar libro por titulo");
        System.out.println("    2 - Mostrar libros registrados");
        System.out.println("    3 - Mostrar autores registrados");
        System.out.println("    4 - Mostrar autores vivos en un año determinado");
        System.out.println("    5 - Mostrar libros por idioma");
        System.out.println("    0 - Salir");
        System.out.println();
        System.out.println();
    }

    private void buscarLibroPorTitulo() {
        System.out.print("Título del libro: ");
        String titulo = sc.nextLine().trim();
        if (titulo.isEmpty()) {
            System.out.println("El título no puede estar vacío.");
            return;
        }

        String json = api.obtenerDatos(URL_BASE + "?search=" + titulo.replace(" ", "%20"));
        Biblioteca biblioteca = conversor.obtenerDatos(json, Biblioteca.class);

        if (biblioteca.resultados() == null || biblioteca.resultados().isEmpty()) {
            System.out.println("No se encontraron resultados para: " + titulo);
            return;
        }

        // Primer resultado de Gutendx
        DatosLibro datos = biblioteca.resultados().get(0);

        // Verificar si el libro ya existe
        if (libroRepository.existsByTitulo(datos.titulo())) {
            System.out.println("El libro ya está registrado en la base de datos.");
            return;
        }

        // DatosLibro -> LibroDTO
        LibroDTO libroDTO = new LibroDTO(
                datos.titulo(),
                datos.autores().stream()
                        .map(a -> new AutorDTO(a.nombre(), a.fechaNacimiento(), a.fechaFallecimiento()))
                        .toList(),
                datos.idiomas(),
                datos.cantidadDescargas()
        );

        // Autor principal: el primero si existe
        Autor autor = null;
        if (libroDTO.autores() != null && !libroDTO.autores().isEmpty()) {
            AutorDTO a = libroDTO.autores().get(0);
            autor = buscarOCrearAutor(a);
        }

        // Construir Libro desde el DTO
        Libro libro = new Libro(libroDTO);

        // Asignar autor único
        if (autor != null) {
            libro.setAutor(autor);
        }

        libroRepository.save(libro);
        System.out.println("\n--- Libro guardado ---");
        System.out.println(libro);
        System.out.println();
    }

    private void listarLibros() {
        var libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        System.out.println("\n--- Libros registrados ---");
        libros.forEach(System.out::println);
        System.out.println();
    }

    private void listarAutores() {
        var autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }
        System.out.println("\n--- Autores registrados ---");
        autores.forEach(System.out::println);
        System.out.println();
    }

    private void autoresVivosPorAnio() {
        System.out.print("Ingrese el año: ");
        String input = sc.nextLine();
        try {
            int anio = Integer.parseInt(input);
            var autores = autorRepository.buscarAutoresVivosEnAnio(anio);
            if (autores.isEmpty()) {
                System.out.println("Ningún autor registrado estaba vivo en " + anio);
                return;
            }
            System.out.println("\n--- Autores vivos en " + anio + " ---");
            autores.forEach(System.out::println);
            System.out.println();
        } catch (NumberFormatException e) {
            System.out.println("El año debe ser un número válido.");
        }
    }

    private void librosPorIdioma() {
        System.out.println("""
                Idiomas disponibles:
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugués
                """);
        System.out.print("Seleccione el idioma: ");
        String idioma = sc.nextLine().trim().toLowerCase();
        if (idioma.isEmpty()) {
            System.out.println("El idioma no puede estar vacío.");
            return;
        }

        var libros = libroRepository.buscarLibrosPorIdiomaIgnoreCase(idioma);
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en el idioma: " + idioma);
            return;
        }
        System.out.println("\n--- Libros en " + idioma + " ---");
        libros.forEach(System.out::println);
        System.out.println();
    }

    private Autor buscarOCrearAutor(AutorDTO dto) {
        return autorRepository
                .findByNombreAndFechaNacimientoAndFechaFallecimiento(
                        dto.nombre(), dto.fechaNacimiento(), dto.fechaFallecimiento())
                .orElseGet(() -> {
                    Autor nuevo = new Autor();
                    nuevo.setNombre(dto.nombre());
                    nuevo.setFechaNacimiento(dto.fechaNacimiento());
                    nuevo.setFechaFallecimiento(dto.fechaFallecimiento());
                    return autorRepository.save(nuevo);
                });
    }
}