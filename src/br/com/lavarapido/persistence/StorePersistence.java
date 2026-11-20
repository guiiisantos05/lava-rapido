package br.com.lavarapido.persistence;

import br.com.lavarapido.model.Store;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorePersistence {
    private final Path file = Path.of("data", "lavarapido.dat");
    public Store load() {
        if (!Files.exists(file)) return new Store();
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(file))) { return (Store) input.readObject(); }
        catch (Exception exception) { return new Store(); }
    }
    public void save(Store store) {
        try {
            Files.createDirectories(file.getParent());
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(file))) { output.writeObject(store); }
        } catch (IOException exception) { throw new IllegalStateException("Não foi possível salvar os dados."); }
    }
}
