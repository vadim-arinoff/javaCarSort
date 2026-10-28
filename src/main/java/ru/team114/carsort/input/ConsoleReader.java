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
                List<Car> cars = mapper.readValue(input, new TypeReference<List<Car>>() {
                });
                System.out.println("JSON successfully recognized. Cars loaded: " + cars.size());
                return cars;
            } catch (Exception e) {
                System.out.println("Failed to parse JSON (" + e.getMessage() + ").");
                System.out.println("→ Switching to manual step-by-step input.\n");
            }
        }

        List<Car> cars = new ArrayList<>();

        System.out.print("How many cars do you want to add manually? (Enter 0 for interactive mode): ");
        int targetCount = 0;
        try {
            String countStr = readLine();
            targetCount = Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number, defaulting to interactive mode.");
        }

        boolean keepAdding = true;
        String currentModel = wasJsonAttempt ? "" : input;
        int carIndex = 1;

        while (keepAdding) {
            if (targetCount > 0) {
                System.out.println("--- Car " + carIndex + " of " + targetCount + " ---");
            } else {
                System.out.println("--- Car " + carIndex + " ---");
            }

            String model;
            if (currentModel.isEmpty()) {
                System.out.print("Car model: ");
                model = readLine();
            } else {
                model = currentModel;
                System.out.println("Car model: " + model);
            }

            int power = readPositiveInt("Power (integer > 0): ");
            int year = readYear("Year of manufacture (1886-" + java.time.Year.now().getValue() + "): ");

            try {
                cars.add(new Car.Builder()
                        .power(power)
                        .modelName(model)
                        .productionYear(year)
                        .build());
                System.out.println("Car '" + model + "' added.\n");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid car data: " + e.getMessage() + " Please try again for this car.\n");
                continue;
            }

            currentModel = "";

            if (targetCount > 0) {
                if (carIndex >= targetCount) {
                    keepAdding = false;
                } else {
                    carIndex++;
                }
            } else {
                System.out.print("Add another car? (y/n): ");
                String answer = readLine().toLowerCase();
                keepAdding = answer.equals("y") || answer.equals("yes") || answer.equals("д");
                System.out.println();
                carIndex++;
            }
        }

        return cars;
    }

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