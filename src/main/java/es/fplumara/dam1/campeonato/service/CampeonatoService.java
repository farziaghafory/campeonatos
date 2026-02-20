package es.fplumara.dam1.campeonato.service;

import es.fplumara.dam1.campeonato.exception.DuplicadoException;
import es.fplumara.dam1.campeonato.exception.NoEncontradoException;
import es.fplumara.dam1.campeonato.exception.OperacionNoPermitidaException;
import es.fplumara.dam1.campeonato.model.Deportista;
import es.fplumara.dam1.campeonato.model.LineaRanking;
import es.fplumara.dam1.campeonato.model.Resultado;
import es.fplumara.dam1.campeonato.repository.DeportistaRepository;
import es.fplumara.dam1.campeonato.repository.ResultadoRepository;

import java.util.*;
import java.util.stream.Collectors;

public class CampeonatoService {
    private final DeportistaRepository deportistaRepo;
    private final ResultadoRepository resultadoRepo;

    public CampeonatoService(DeportistaRepository deportistaRepo, ResultadoRepository resultadoRepo) {
        this.deportistaRepo = Objects.requireNonNull(deportistaRepo, "deportistaRepo cannot be null");
        this.resultadoRepo = Objects.requireNonNull(resultadoRepo, "resultadoRepo cannot be null");
    }
    // Register deportista
    public void registrarDeportista(Deportista d) {
        if (d == null) throw new IllegalArgumentException("Deportista cannot be null");
        if (d.getId() == null || d.getId().isBlank() ||
                d.getNombre() == null || d.getNombre().isBlank() ||
                d.getPais() == null || d.getPais().isBlank()) {
            throw new IllegalArgumentException("Deportista id/nombre/pais cannot be null or empty");
        }

        if (deportistaRepo.findById(d.getId()).isPresent()) {
            throw new DuplicadoException("Deportista with id " + d.getId() + " already exists");
        }

        deportistaRepo.save(d);
    }
    public void registrarResultado(Resultado r) {
        if (r == null) throw new IllegalArgumentException("Resultado cannot be null");
        if (r.getId() == null || r.getId().isBlank() ||
                r.getIdPrueba() == null || r.getIdPrueba().isBlank() ||
                r.getIdDeportista() == null || r.getIdDeportista().isBlank()) {
            throw new IllegalArgumentException("Resultado id/idPrueba/idDeportista cannot be null or empty");
        }

        if (r.getTipoPrueba() == null) throw new IllegalArgumentException("tipoPrueba cannot be null");
        if (r.getPosicion() <= 0) throw new IllegalArgumentException("posicion must be > 0");

        if (resultadoRepo.findById(r.getId()).isPresent()) {
            throw new DuplicadoException("Resultado with id " + r.getId() + " already exists");
        }

        if (deportistaRepo.findById(r.getIdDeportista()).isEmpty()) {
            throw new NoEncontradoException("Deportista with id " + r.getIdDeportista() + " not found");
        }

        //one result per athlete per test
        if (resultadoRepo.existsByPruebaYDeportista(r.getIdPrueba(), r.getIdDeportista())) {
            throw new OperacionNoPermitidaException("Deportista " + r.getIdDeportista() +
                    " already has a result for prueba " + r.getIdPrueba());
        }

        resultadoRepo.save(r);
    }
    public List<LineaRanking> ranking() {
        // Map: idDeportista == puntos totales
        Map<String, Integer> puntosPorDeportista = new HashMap<>();

        for (Resultado r : resultadoRepo.listAll()) {
            puntosPorDeportista.merge(r.getIdDeportista(), r.getPuntos(), Integer::sum);
        }
        // Transform into LineaRanking
        List<LineaRanking> ranking = puntosPorDeportista.entrySet().stream()
                .map(entry -> {
                    Deportista d = deportistaRepo.findById(entry.getKey()).orElseThrow();
                    return new LineaRanking(d.getId(), d.getNombre(), d.getPais(), entry.getValue());
                })
                .sorted(Comparator.comparingInt(LineaRanking::getPuntos).reversed()) // descending
                .toList();

        return ranking;
    }
    // filtrar por pais
    public List<Resultado> resultadosDePais(String pais) {
        if (pais == null || pais.isBlank()) return List.of();
        List<Deportista> deportistasPais = deportistaRepo.findByPais(pais);
        Set<String> ids = deportistasPais.stream()
                .map(Deportista::getId)
                .collect(Collectors.toSet());

        return resultadoRepo.listAll().stream()
                .filter(r -> ids.contains(r.getIdDeportista()))
                .toList();
    }
    // Paises participantes
    public Set<String> paisesParticipantes() {
        return deportistaRepo.listAll().stream()
                .map(Deportista::getPais)
                .collect(Collectors.toSet());
    }

}
