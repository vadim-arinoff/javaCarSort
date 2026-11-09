package ru.team114.carsort;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.input.Input;
import ru.team114.carsort.model.Car;
import ru.team114.carsort.multithreading.CarCounter;
import ru.team114.carsort.output.FileOutput;
import ru.team114.carsort.output.Output;
import ru.team114.carsort.sorting.BubbleSortStrategy;
import ru.team114.carsort.sorting.CarComparators;
import ru.team114.carsort.sorting.EvenOddSorter;
import ru.team114.carsort.sorting.SelectionSortStrategy;
import ru.team114.carsort.sorting.SortStrategy;

import java.time.Year;
import java.util.Comparator;
import java.util.Scanner;

public class Main {

    private final Scanner scanner;
    private final Input input;
    private final Output output;
    private CustomCollection<Car> cars = new CustomCollection<>();

    public Main(Scanner scanner) {
        this.scanner = scanner;
        this.input = new Input(scanner, new ObjectMapper());
        this.output = new FileOutput();
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Main app = new Main(scanner);
            app.run();
        }
    }

    private void run() {
        while (true) {
            printMenu();
            int command = readChoice("Выберите пункт: ", 0, 5);

            switch (command) {
                case 1 -> loadCars();
                case 2 -> showCars();
                case 3 -> sortCars();
                case 4 -> saveCars();
                case 5 -> countCars();

                case 0 -> {
                    System.out.println("Выход из программы...");
                    return;
                }

                default -> System.out.println("Неверный выбор. Попробуйте снова." + command);
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Сортировка автомобилей ===");
        System.out.println("   Сейчас загружено автомобилей: " + cars.size());
        System.out.println("1. Загрузить автомобили");
        System.out.println("2. Показать автомобили");
        System.out.println("3. Сортировать автомобили");
        System.out.println("4. Сохранить автомобили в файл");
        System.out.println("5. Подсчитать количество автомобилей по критериям");
        System.out.println("0. Выход\n");
    }

    private void loadCars() {
        try {
            CustomCollection<Car> loadedCars = input.execute();
            cars = loadedCars;
            System.out.println("Автомобили успешно загружены. Всего автомобилей: " + cars.size());
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке автомобилей: " + e.getMessage());
        }
    }

    private void showCars() {
        if (cars.isEmpty()) {
            System.out.println("Нет загруженных автомобилей для отображения.");
            return;
        }

        for (int i = 0; i < cars.size(); i++) {
            System.out.println((i + 1) + ". " + cars.get(i));
        }
    }

    private void sortCars() {
        if(cars.isEmpty()) {
            System.out.println("Нет загруженных автомобилей для сортировки.");
            return;
        }

        System.out.println("Выберите алгоритм:");
        System.out.println("1. Пузырьковая сортировка");
        System.out.println("2. Сортировка выбором\n");
        int algorithm = readChoice("Алгоритм: ", 1, 2);

        SortStrategy<Car> strategy = switch (algorithm) {
            case 1 -> new BubbleSortStrategy<>();
            case 2 -> new SelectionSortStrategy<>();
            default -> throw new IllegalArgumentException("Неверный выбор алгоритма\n");
        };

        System.out.println("Выберите режим:");
        System.out.println("1. Обычная сортировка");
        System.out.println("2. Сортировка автомобилей только с чётной мощностью\n");
        int mode = readChoice("Режим: ", 1, 2);

        if (mode == 2) {
            SortStrategy<Car> evenStrategy = new EvenOddSorter<>(Car::getPower, strategy);
            evenStrategy.sort(cars, CarComparators.BY_POWER);
        } else {
            System.out.println("По какому полю сортировать?");
            System.out.println("1. Мощность");
            System.out.println("2. Название модели");
            System.out.println("3. Год производства");
            int field = readChoice("Поле: ", 1, 3);

            Comparator<Car> comparator = switch (field) {
                case 1 -> CarComparators.BY_POWER;
                case 2 -> CarComparators.BY_MODEL_NAME;
                case 3 -> CarComparators.BY_PRODUCTION_YEAR;
                default -> throw new IllegalArgumentException("Неверный выбор поля");
            };

            strategy.sort(cars, comparator);
        }

        System.out.println("Автомобили успешно отсортированы:");
        showCars();
    }

    private void saveCars() {
        if (cars.isEmpty()) {
            System.out.println("Нет загруженных автомобилей для сохранения.");
            return;
        }

        System.out.print("Путь к файлу результата | (Enter - results.json): ");
        String path = scanner.nextLine().trim();

        output.write(cars, path);
    }

    private void countCars() {
        if (cars.isEmpty()) {
            System.out.println("Сначала загрузите автомобили для подсчета.");
            return;
        }

        System.out.println("Введите все три поля автомобиля, вхождения которого нужно найти:");

        int power = readPositiveInt("Мощность: ");
        String modelName = readNonBlank("Название модели: ");
        int productionYear = readChoice(
            "Год производства: ",
            1886, 
            Year.now().getValue()
        );

        Car target = new Car.Builder()
            .power(power)
            .modelName(modelName)
            .productionYear(productionYear)
            .build();

            CarCounter.countOccurrences(cars, target);
    }

    private int readPositiveInt(String prompt) {
        return readChoice(prompt, 1, Integer.MAX_VALUE);
    }

    private String readNonBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();

            if (!value.isBlank()) {
                return value;
            }

            System.out.println("Ввод не может быть пустым.");
        }
    }

    private int readChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();

            try {
                int number = Integer.parseInt(line);

                if (number >= min && number <= max) {
                    return number;
                } 
                    
                System.out.println("Пожалуйста, введите число от " + min + " до " + max);
                
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }
    }


}