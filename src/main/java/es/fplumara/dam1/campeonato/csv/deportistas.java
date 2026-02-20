package es.fplumara.dam1.campeonato.csv;

import es.fplumara.dam1.campeonato.exception.FicheroInvalidoException;
import es.fplumara.dam1.campeonato.model.Deportista;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class deportistas {
    public List<Deportista> read(Path path) {
        try (Reader reader = Files.newBufferedReader(path); // csv parser read, interpret and process
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {

            List<Deportista> lista = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                String id = record.get("id");
                String nombre = record.get("nombre");
                String pais = record.get("pais");

                lista.add(new Deportista(id, nombre, pais));
            }

            return lista;

        } catch (IOException e) {
            throw new FicheroInvalidoException("Can not find the CSV", e);
        }
    }
}


/*
























/*
package es.fplumara.dam1.campeonato.io;

import es.fplumara.dam1.campeonato.exception.FicheroInvalidoException;
import es.fplumara.dam1.campeonato.model.*;
import org.apache.commons.csv.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ResultadoCsvIO {

    public List<Resultado> leer(Path path) {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {

            List<Resultado> lista = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                String id = record.get("id");
                String idPrueba = record.get("idPrueba");
                TipoPrueba tipo = TipoPrueba.valueOf(record.get("tipoPrueba"));
                String idDeportista = record.get("idDeportista");
                int posicion = Integer.parseInt(record.get("posicion"));

                lista.add(new Resultado(id, idPrueba, tipo, idDeportista, posicion));
            }

            return lista;

        } catch (IOException e) {
            throw new FicheroInvalidoException("Error reading resultados.csv", e);
        }
    }
}




package es.fplumara.dam1.campeonato.io;

import es.fplumara.dam1.campeonato.exception.FicheroInvalidoException;
import es.fplumara.dam1.campeonato.model.LineaRanking;
import org.apache.commons.csv.*;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RankingCsvWriter {

    public void escribir(Path path, List<LineaRanking> ranking) {

        try (Writer writer = Files.newBufferedWriter(path);
             CSVPrinter printer = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.withHeader("idDeportista", "nombre", "pais", "puntos"))) {

            for (LineaRanking l : ranking) {
                printer.printRecord(
                        l.getIdDeportista(),
                        l.getNombre(),
                        l.getPais(),
                        l.getPuntos()
                );
            }

        } catch (IOException e) {
            throw new FicheroInvalidoException("Error writing ranking.csv", e);
        }
    }
}

