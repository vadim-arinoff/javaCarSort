package ru.team114.carsort.input;

import ru.team114.carsort.model.Car;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RandomReader implements Reader {
    private final Scanner scanner;
    private final Random random = new Random();

    private static class CarModelSpec {
        final String modelName;
        final int minPower, maxPower;
        final int minYear, maxYear;

        CarModelSpec(String modelName, int minPower, int maxPower, int minYear, int maxYear) {
            this.modelName = modelName;
            this.minPower = minPower;
            this.maxPower = maxPower;
            this.minYear = minYear;
            this.maxYear = maxYear;
        }
    }

    private static final List<CarModelSpec> SPECS = List.of(
        new CarModelSpec("Lada Vesta", 90, 150, 2015, 2024),
        new CarModelSpec("Toyota Camry", 150, 250, 2010, 2024),
        new CarModelSpec("BMW X5", 250, 400, 2015, 2024),
        new CarModelSpec("Kia Rio", 90, 123, 2015, 2024),
        new CarModelSpec("Ford Focus", 100, 150, 2010, 2020),
        new CarModelSpec("Mazda RX-7", 255, 280, 1992, 2002),
        new CarModelSpec("Mazda RX-8", 190, 250, 2003, 2012),
        new CarModelSpec("Ford Mustang 1967", 115, 355, 1964, 1973),
        new CarModelSpec("Dodge Challenger 1970", 145, 425, 1970, 1974),
        new CarModelSpec("Chevrolet Corvette C6", 405, 640, 2004, 2013)
    );

    public RandomReader(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public List<Car> read() {
        System.out.print("Enter the number of cars to generate randomly: ");
        int count;
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                System.out.println(line);
                count = Integer.parseInt(line);
                if (count > 0) break;
                System.out.print("Number must be greater than 0. Try again: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter an integer: ");
            }
        }

        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CarModelSpec spec = SPECS.get(random.nextInt(SPECS.size()));
            
            int power = spec.minPower + random.nextInt(spec.maxPower - spec.minPower + 1);
            int year = spec.minYear + random.nextInt(spec.maxYear - spec.minYear + 1);

            try {
                cars.add(new Car.Builder()
                        .modelName(spec.modelName)
                        .power(power)
                        .productionYear(year)
                        .build());
            } catch (IllegalArgumentException e) {
                i--;
            }
        }
        System.out.println("Successfully generated " + cars.size() + " random cars.");
        return cars;
    }
}