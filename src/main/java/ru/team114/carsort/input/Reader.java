package ru.team114.carsort.input;

import java.util.List;

import ru.team114.carsort.model.Car;

public interface Reader {
    List<Car> read();
}