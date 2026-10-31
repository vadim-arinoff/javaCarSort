package ru.team114.carsort.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;
import java.util.Scanner;

public class Input {

    private final Scanner scanner;
    private final ObjectMapper mapper;

    public Input(Scanner scanner, ObjectMapper mapper) {
        this.scanner = scanner;
        this.mapper = mapper;
        this.mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    public CustomCollection<Car> execute() {
        System.out.println("Введите данные:");
        System.out.println(" - имя файла (cars.json) или полный путь");
        System.out.println(" - JSON (один объект {...} или массив [{...}])");
        System.out.println(" - 'random' или 'rand' для случайной генерации");
        System.out.println(" - или просто название модели для ручного ввода");
        System.out.print("> ");
        
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            throw new IllegalArgumentException("Пустой ввод. Пожалуйста, попробуйте снова.");
        }

        Reader reader = resolveReader(input);
        return reader.read();
    }

    private Reader resolveReader(String input) {
        if (input.equalsIgnoreCase("random") || input.equalsIgnoreCase("rand") || input.equalsIgnoreCase("случайно")) {
            System.out.println("Режим: Случайная генерация");
            return new RandomReader(scanner);
        }

        if (input.matches("^[A-Za-z]:[/\\\\].*") || input.startsWith("/") || input.startsWith("\\")
                || input.startsWith("\\\\")) {
            System.out.println("Режим: Чтение файла по абсолютному пути");
            return new FileReader(input, mapper);
        }

        if (input.toLowerCase().endsWith(".json") && !input.contains("/") && !input.contains("\\")) {
            System.out.println("Режим: Чтение из classpath (resources)");
            return new ResourceReader(input, mapper);
        }

        System.out.println("Режим: Консольный ввод (JSON или ручной)");
        return new ConsoleReader(scanner, input, mapper);
    }
}