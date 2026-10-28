package ru.team114.carsort.input.temp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Car extends AbstractCar {

    @JsonCreator
    public Car(@JsonProperty("power") int power,
               @JsonProperty("model") String model,
               @JsonProperty("year") int year) {
        super(power, model, year);
    }
}