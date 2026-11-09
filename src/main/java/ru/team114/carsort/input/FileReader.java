package ru.team114.carsort.input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class FileReader implements Reader {
    private final String path;
    private final ObjectMapper mapper;
    private final int n;

    public FileReader(String path, ObjectMapper mapper, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Количество запрашиваемых автомобилей (n) должно быть больше 0");
        }
        this.path = path;
        this.mapper = mapper;
        this.n = n;
    }

    @Override
    public CustomCollection<Car> read() {
        File file = new File(path);

        if (!file.exists()) {
            throw new RuntimeException("File not found: " + path);
        }

        if (path.toLowerCase().endsWith(".jsonl")) {
            return readJsonLines(file);
        }

        try {
            List<Car> list = mapper.readValue(file, new TypeReference<List<Car>>() {});
            CustomCollection<Car> result = new CustomCollection<>();
            
            list.stream()
                .limit(n)
                .forEach(result::add);

            if (result.size() < n) {
                throw new IllegalArgumentException("В файле меньше запрошенных автомобилей. Частичная коллекция не возвращается. (Запрошено: " + n + ", найдено: " + result.size() + ")");
            }
            
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage(), e);
        }
    }

    private CustomCollection<Car> readJsonLines(File file) {
        CustomCollection<Car> result = new CustomCollection<>();

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            int currentLine = 0;

            while ((line = reader.readLine()) != null && result.size() < n) {
                currentLine++;

                if (line.isBlank()) {
                    throw new IllegalArgumentException("Пустая строка в файле: " + currentLine);
                }

                try {
                    Car car = mapper.readValue(line, Car.class);

                    if (car == null) {
                        throw new IllegalArgumentException("В строке " + currentLine + " нет автомобиля");
                    }

                    result.add(car);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("Ошибка JSON в строке " + currentLine + ": " + e.getOriginalMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл: " + path, e);
        }

        if (result.size() < n) {
            throw new IllegalArgumentException("В файле меньше запрошенных автомобилей. Частичная коллекция не возвращается. (Запрошено: " + n + ", найдено: " + result.size() + ")");
        }

        return result;
    }
}