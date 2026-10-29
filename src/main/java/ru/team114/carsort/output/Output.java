package ru.team114.carsort.output;

import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

public interface Output {
    void write(CustomCollection<Car> data, String filePath);

    default void writeSingle(Car car, String filePath) {
        if (car != null) {
            CustomCollection<Car> singleCollection = new CustomCollection<>(1);
            singleCollection.add(car);
            write(singleCollection, filePath);
        }
    }
}