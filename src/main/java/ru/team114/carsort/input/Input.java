package ru.team114.carsort.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.model.Car;
import java.util.List;
import java.util.Scanner;

public class Input {

    private final Scanner scanner;
    private final ObjectMapper mapper;

    public Input(Scanner scanner, ObjectMapper mapper) {
        this.scanner = scanner;
        this.mapper = mapper;
        this.mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    public List<Car> execute() {
        System.out.println("Enter data:");
        System.out.println(" file name (cars.json) or full path");
        System.out.println(" JSON (a single object {...} or an array [{...}])");
        System.out.println(" or just the model name for manual input");
        System.out.print("> ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            throw new IllegalArgumentException("Empty input.");
        }

        Reader reader = resolveReader(input);
        return reader.read();
    }

    private Reader resolveReader(String input) {
        if (input.equalsIgnoreCase("random") || input.equalsIgnoreCase("rand") || input.equalsIgnoreCase("случайно")) {
            System.out.println("Mode: Randomized generation");
            return new RandomReader(scanner);
        }

        if (input.matches("^[A-Za-z]:[/\\\\].*") || input.startsWith("/") || input.startsWith("\\")
                || input.startsWith("\\\\")) {
            System.out.println("Mode: Reading file by path");
            return new FileReader(input, mapper);
        }

        if (input.toLowerCase().endsWith(".json") && !input.contains("/") && !input.contains("\\")) {
            System.out.println("Mode: Reading from classpath (resources)");
            return new ResourceReader(input, mapper);
        }

        System.out.println("Mode: Console input (JSON or manual)");
        return new ConsoleReader(scanner, input, mapper);
    }
}