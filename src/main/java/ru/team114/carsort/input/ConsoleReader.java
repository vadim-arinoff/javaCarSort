package ru.team114.carsort.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.model.Car;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleReader implements Reader {

    private final Scanner scanner;
    private final String firstLine;
    private final ObjectMapper mapper;

    public ConsoleReader(Scanner scanner, String firstLine, ObjectMapper mapper) {
        this.scanner = scanner;
        this.firstLine = firstLine;
        this.mapper = mapper;
    }

    @Override
    public List<Car> read() {
        String input = firstLine.trim();
        boolean wasJsonAttempt = input.startsWith("{") || input.startsWith("[");
        
        if (wasJsonAttempt) {
            try {
                List<Car> cars = mapper.readValue(input, new TypeReference<List<Car>>() {});
                System.out.println("JSON successfully recognized. Cars loaded: " + cars.size());
                return cars;
            } catch (Exception e) {
                System.out.println("Failed to parse JSON (" + e.getMessage() + ").");
                System.out.println("→ Switching to manual step-by-step input.\n");
            }
        }
        
        List<Car> cars = new ArrayList<>();
        boolean keepAdding = true;
        String currentModel = wasJsonAttempt ? "" : input;
        int currentYear = Year.now().getValue();
        
        while (keepAdding) {
            String model;
            if (currentModel.isEmpty()) {
                System.out.print("Car model: ");
                model = readLine();
            } else {
                model = currentModel;
                // Явно печатаем предзаполненную модель с переносом строки
                System.out.println("Car model: " + model);
            }

            int power = readPositiveInt("Power (integer > 0): ");
            int year = readYear("Year of manufacture (1886-" + currentYear + "): ");
            
            try {
                cars.add(new Car.Builder()
                        .power(power)
                        .modelName(model)
                        .productionYear(year)
                        .build());
                System.out.println("Car '" + model + "' added.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid car data: " + e.getMessage() + " Please try again for this car.\n");
                continue; // Повторяем ввод для текущего автомобиля
            }
            
            currentModel = ""; // Сбрасываем для следующих итераций
            
            System.out.print("Add another car? (y/n): ");
            String answer = readLine().toLowerCase();
            keepAdding = answer.equals("y") || answer.equals("yes") || answer.equals("д");
            System.out.println(); // Пустая строка для визуального разделения
        }
        
        return cars;
    }

    /**
     * Читает строку и выводит её обратно (эхо).
     * Это делает вывод в JUnit-тестах таким же читаемым, как в реальном терминале.
     */
    private String readLine() {
        String line = scanner.nextLine().trim();
        System.out.println(line);
        return line;
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = readLine();
            try {
                int val = Integer.parseInt(line);
                if (val <= 0) {
                    System.out.println("Must be > 0. Please try again.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Expected an integer. Please try again.");
            }
        }
    }

    private int readYear(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = readLine();
            try {
                int val = Integer.parseInt(line);
                int currentYear = Year.now().getValue();
                if (val < 1886 || val > currentYear) {
                    System.out.println("Year must be between 1886 and " + currentYear + ". Please try again.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Expected an integer. Please try again.");
            }
        }
    }
}