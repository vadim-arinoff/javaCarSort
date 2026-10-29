package ru.team114.carsort.input;

import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

public interface Reader {
    CustomCollection<Car> read();
}