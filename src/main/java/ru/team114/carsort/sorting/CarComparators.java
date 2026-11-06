package ru.team114.carsort.sorting;

import java.util.Comparator;
import ru.team114.carsort.model.Car;

public final class CarComparators {

    public static final Comparator<Car> BY_POWER =
            Comparator.comparingInt(Car::getPower);

    public static final Comparator<Car> BY_MODEL_NAME =
            Comparator.comparing(Car::getModelName);

    public static final Comparator<Car> BY_PRODUCTION_YEAR =
            Comparator.comparingInt(Car::getProductionYear);

    private CarComparators() {
    }
}