package es.fplumara.dam1.campeonato.repository;

import es.fplumara.dam1.campeonato.model.Deportista;

import java.util.*;
import java.util.stream.Collectors;

public class DeportistaRepositoryImpl implements DeportistaRepository{
    private final Map<String, Deportista> datos = new HashMap<>();
    @Override
    public void save(Deportista d) {
        datos.put(d.getId(), d);
    }

    @Override
    public Optional<Deportista> findById(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<Deportista> listAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public List<Deportista> findByPais(String pais) {
        return datos.values().stream()
                .filter(d -> d.getPais().equalsIgnoreCase(pais))
                .collect(Collectors.toList());
    }
}
