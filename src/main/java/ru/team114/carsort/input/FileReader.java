package ru.team114.carsort.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.model.Car;
import ru.team114.carsort.collection.CustomCollection;
import java.io.File;
import java.util.List;

public class FileReader implements Reader {

    private final String path;
    private final ObjectMapper mapper;

    public FileReader(String path, ObjectMapper mapper) {
        this.path = path;
        this.mapper = mapper;
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
            CustomCollection<Car> cars = new CustomCollection<>();
            list.stream().forEach(cars::add);
            return cars;
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + e.getMessage(), e);
        }
    }
}