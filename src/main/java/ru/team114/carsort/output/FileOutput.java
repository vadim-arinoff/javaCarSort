package ru.team114.carsort.output;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileOutput implements Output {
    private final ObjectMapper mapper;
    
    private static final String DEFAULT_FILE_NAME = "results.json";

    public FileOutput() {
        this.mapper = new ObjectMapper();
        this.mapper.writerWithDefaultPrettyPrinter(); 
    }

    @Override
    public void write(CustomCollection<Car> data, String filePath) {
        if (data == null || data.isEmpty()) {
            System.out.println("Нет данных для записи в файл.");
            return;
        }

        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = DEFAULT_FILE_NAME;
        } else {
            filePath = filePath.trim();
            if (!filePath.toLowerCase().endsWith(".json")) {
                int lastDot = filePath.lastIndexOf('.');
                if (lastDot > 0) {
                    filePath = filePath.substring(0, lastDot) + ".json";
                } else {
                    filePath = filePath + ".json";
                }
            }
        }

        Path path = Paths.get(filePath);
        List<Car> carsToSave = new ArrayList<>();

        try {
            if (Files.exists(path) && Files.size(path) > 0) {
                try {
                    carsToSave = mapper.readValue(path.toFile(), new TypeReference<List<Car>>() {});
                } catch (IOException e) {
                    System.err.println("Файл существует, но поврежден или пуст. Начинаем запись заново.");
                    carsToSave = new ArrayList<>();
                }
            } else {
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                System.out.println("Создан новый файл: " + filePath);
            }

            for (int i = 0; i < data.size(); i++) {
                carsToSave.add(data.get(i));
            }

            mapper.writeValue(path.toFile(), carsToSave);
            
            System.out.println("Успешно добавлено " + data.size() + " записей в файл: " + filePath + " (всего в файле: " + carsToSave.size() + ")");

        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
        }
    }
}