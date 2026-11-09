package ru.team114.carsort.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.io.File;
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
        File file = new File(input);

        if (file.isFile() || file.isAbsolute()) {
            System.out.println("Режим: чтение файла");
            int n = requestCarCount(scanner);
            return new FileReader(input, mapper, n);
        }

        if (input.equalsIgnoreCase("random") || input.equalsIgnoreCase("rand")) {
            System.out.println("Режим: случайная генерация");
            return new RandomReader(scanner);
        }

        if (input.toLowerCase().endsWith(".json") && !input.contains("/") && !input.contains("\\")) {
            System.out.println("Режим: чтение JSON файла из resources");
            return new ResourceReader(input, mapper);
        }

        System.out.println("Режим: ручной ввод");
        return new ConsoleReader(scanner, input, mapper);
    }

    private int requestCarCount(Scanner scanner) {
        while (true) {
            System.out.print("Введите количество машин для загрузки (N): ");
            String countInput = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(countInput);
                if (n > 0) {
                    return n;
                }
                System.out.println("Количество должно быть больше 0. Попробуйте снова.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное целое число.");
            }
        }
    }
}