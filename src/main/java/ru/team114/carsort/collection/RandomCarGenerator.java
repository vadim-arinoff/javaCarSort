package ru.team114.carsort.collection;

import ru.team114.carsort.model.Car;

import java.time.Year;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class RandomCarGenerator {

    private static final List<String> MODEL_NAMES = List.of(
            "Toyota Camry",
            "BMW X5",
            "Audi A6",
            "Volkswagen Polo",
            "Honda Civic",
            "Mercedes-Benz E-Class",
            "Ford Mustang",
            "Tesla Model 3",
            "Skoda Octavia",
            "Kia Sportage"
    );

    public CustomCollection<Car> generate(int count) {
        if (count < 0) {
            throw new IllegalArgumentException(
                    "Количество автомобилей не может быть отрицательным."
            );
        }

        CustomCollection<Car> cars = new CustomCollection<>();

        IntStream.range(0, count)
                .mapToObj(i -> generateCar())
                .forEach(cars::add);

        return cars;
    }

    private Car generateCar() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int power = random.nextInt(50, 1001);

        String modelName = MODEL_NAMES.get(
                random.nextInt(MODEL_NAMES.size())
        );

        int currentYear = Year.now().getValue();
        int productionYear = random.nextInt(1886, currentYear + 1);

        return new Car.Builder()
                .power(power)
                .modelName(modelName)
                .productionYear(productionYear)
                .build();
    }
}