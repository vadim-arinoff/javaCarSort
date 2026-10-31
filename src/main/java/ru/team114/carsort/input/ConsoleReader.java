package ru.team114.carsort.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.collection.CustomCollection;
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
    public CustomCollection<Car> read() {
        String input = firstLine.trim();
        boolean wasJsonAttempt = input.startsWith("{") || input.startsWith("[");

        if (wasJsonAttempt) {
            try {
                List<Car> tempList = mapper.readValue(input, new TypeReference<List<Car>>() {
                });

                CustomCollection<Car> cars = new CustomCollection<>(tempList.size());
                tempList.stream().forEach(cars::add);

                System.out.println("JSON успешно распознан. Загружено машин: " + tempList.size());
                return cars;
            } catch (Exception e) {
                System.out.println("Не удалось распознать JSON (" + e.getMessage() + ").");
                System.out.println("Переключаюсь на ручной пошаговый ввод.\n");
            }
        }

        List<Car> tempList = new ArrayList<>();

        System.out.print("Сколько машин вы хотите добавить? (Введите 0 для интерактивного режима): ");
        int targetCount = 0;
        try {
            String countStr = readLine();
            targetCount = Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
            System.out.println("Неверное число, переключаюсь на интерактивный режим.");
        }

        boolean keepAdding = true;
        String currentModel = wasJsonAttempt ? "" : input;
        int carIndex = 1;

        while (keepAdding) {
            if (targetCount > 0) {
                System.out.println("--- Машина " + carIndex + " из " + targetCount + " ---");
            } else {
                System.out.println("--- Машина " + carIndex + " ---");
            }

            String model;
            if (currentModel.isEmpty()) {
                System.out.print("Модель автомобиля: ");
                model = readLine();
            } else {
                model = currentModel;
                System.out.println("Модель автомобиля: " + model);
            }

            int power = readPositiveInt("Мощность (целое число > 0): ");
            int year = readYear("Год выпуска (1886-" + Year.now().getValue() + "): ");

            try {
                Car newCar = new Car.Builder()
                        .power(power)
                        .modelName(model)
                        .productionYear(year)
                        .build();

                tempList.add(newCar);
                System.out.println("Автомобиль '" + model + "' успешно добавлен.\n");

            } catch (IllegalArgumentException e) {
                System.out.println("Неверные данные автомобиля: " + e.getMessage()
                        + " Пожалуйста, попробуйте снова для этой машины.\n");
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
                System.out.print("Добавить еще одну машину? (y/n): ");
                String answer = readLine().toLowerCase();
                keepAdding = answer.equals("y") || answer.equals("yes") || answer.equals("д");
                System.out.println();
                carIndex++;
            }
        }

        CustomCollection<Car> resultCollection = new CustomCollection<>(tempList.size());
        tempList.stream().forEach(resultCollection::add);

        return resultCollection;
    }

    private String readLine() {
        String line = scanner.nextLine().trim();
        return line;
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = readLine();
            try {
                int val = Integer.parseInt(line);
                if (val <= 0) {
                    System.out.println("Значение должно быть > 0. Пожалуйста, попробуйте снова.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Ожидалось целое число. Пожалуйста, попробуйте снова.");
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
                    System.out.println("Год должен быть в диапазоне от 1886 до " + currentYear
                            + ". Пожалуйста, попробуйте снова.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Ожидалось целое число. Пожалуйста, попробуйте снова.");
            }
        }
    }
}