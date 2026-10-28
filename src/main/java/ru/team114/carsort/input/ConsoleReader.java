package ru.team114.carsort.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.team114.carsort.input.temp.Car;

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

        while (keepAdding) {
            System.out.print("Car model: ");
            String model = currentModel.isEmpty() ? scanner.nextLine().trim() : currentModel;
            
            int power = readPositiveInt("Power (integer > 0): ");
            int year = readPositiveInt("Year of manufacture: ");

            cars.add(new Car(power, model, year));
            System.out.println("Car '" + model + "' added.");

            currentModel = "";
            
            System.out.print("Add another car? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            keepAdding = answer.equals("y") || answer.equals("yes") || answer.equals("д");
            System.out.println();
        }

        return cars;
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
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
}