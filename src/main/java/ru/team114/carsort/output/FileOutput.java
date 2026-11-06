package ru.team114.carsort.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileOutput implements Output {
    private final ObjectMapper mapper;
    
    private static final String DEFAULT_FILE_NAME = "results.jsonl";

    public FileOutput() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public void write(CustomCollection<Car> data, String filePath) {
        if (data == null || data.isEmpty()) {
            System.out.println("Нет данных для записи в файл.");
            return;
        }

        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = DEFAULT_FILE_NAME;
        }

        Path path = Paths.get(filePath);

        try {
            if (!Files.exists(path)) {
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                Files.createFile(path);
                System.out.println("Создан новый файл: " + filePath);
            }

            List<String> linesToAppend = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                Car car = data.get(i);
                String jsonLine = mapper.writeValueAsString(car);
                linesToAppend.add(jsonLine);
            }

            Files.write(path, linesToAppend, StandardOpenOption.APPEND);
            System.out.println("Успешно добавлено " + data.size() + " записей в файл: " + filePath);

        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }
}