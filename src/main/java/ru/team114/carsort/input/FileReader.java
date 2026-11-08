package ru.team114.carsort.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.io.File;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import com.fasterxml.jackson.core.JsonProcessingException;

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

        if (path.toLowerCase().endsWith(".jsonl")) {
            return readJsonLines(file);
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

    private CustomCollection<Car> readJsonLines(File file) {
        CustomCollection<Car> result = new CustomCollection<>();
        AtomicInteger lineNumber = new AtomicInteger();

        try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
        
            lines.limit(n).forEach(line -> {
                int currentLine = lineNumber.incrementAndGet();

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
                throw new IllegalArgumentException("Ошибка JSON в строке " + currentLine+ ": " + e.getOriginalMessage(), e);
            }
        });
            } catch (IOException e) {
                throw new RuntimeException("Не удалось прочитать файл: " + path, e);
            }

        if (result.size() < n) {
            throw new IllegalArgumentException("В файле только " + result.size() + " автомобилей, а запрошено " + n);
            }

        return result;
    }
}