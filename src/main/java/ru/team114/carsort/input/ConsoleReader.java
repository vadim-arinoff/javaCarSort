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

        if (input.startsWith("{") || input.startsWith("[")) {
            try {
                List<Car> cars = mapper.readValue(input, new TypeReference<List<Car>>() {});
                System.out.println("✅ Успешно распознан JSON. Загружено машин: " + cars.size());
                return cars;
            } catch (Exception e) {
                System.out.println("⚠️ Не удалось распарсить JSON (" + e.getMessage() + ").");
                System.out.println("→ Переключаюсь на ручной пошаговый ввод.\n");
            }
        }

        List<Car> cars = new ArrayList<>();
        boolean keepAdding = true;
        String currentModel = input;

        while (keepAdding) {
            System.out.print("Модель автомобиля: ");
            String model = currentModel.isEmpty() ? scanner.nextLine().trim() : currentModel;
            
            int power = readPositiveInt("Мощность (целое число > 0): ");
            int year = readPositiveInt("Год выпуска: ");

            cars.add(new Car(power, model, year));
            System.out.println("✅ Машина '" + model + "' добавлена.");

            currentModel = "";
            
            System.out.print("Добавить ещё одну машину? (y/n): ");
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
                    System.out.println("❌ Должно быть > 0. Повторите.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("❌ Ожидалось целое число. Повторите.");
            }
        }
    }
}