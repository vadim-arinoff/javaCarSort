package ru.team114.carsort.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.team114.carsort.input.temp.Car;

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
    public List<Car> read() {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("File not found or is not a file: " + path);
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Car>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + e.getMessage(), e);
        }
    }
}