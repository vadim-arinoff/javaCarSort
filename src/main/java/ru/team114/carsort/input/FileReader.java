package ru.team114.carsort.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.io.File;
import java.util.List;

public class FileReader implements Reader {
    private final String path;
    private final ObjectMapper mapper;
    private final int n;

    public FileReader(String path, ObjectMapper mapper, int n) {
        this.path = path;
        this.mapper = mapper;
        this.n = n;
    }

    @Override
    public CustomCollection<Car> read() {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("File not found: " + path);
        }

        try {
            List<Car> list = mapper.readValue(file, new TypeReference<List<Car>>() {
            });

            if (list == null || list.size() < n) {
                throw new RuntimeException("Ошибка: в файле содержится " + (list == null ? 0 : list.size())
                        + " машин, что меньше запрошенных " + n + ". Частичная коллекция не возвращается.");
            }

            return list.stream()
                    .limit(n)
                    .collect(
                            CustomCollection::new,
                            CustomCollection::add,
                            (c1, c2) -> {
                            });

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + e.getMessage(), e);
        }
    }
}