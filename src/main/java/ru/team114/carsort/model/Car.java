package ru.team114.carsort.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.Year;
import java.util.Objects;

@JsonDeserialize(builder = Car.Builder.class)
public class Car {
    private final int power;
    private final String modelName;
    private final int productionYear;

    private Car(int power, String modelName, int productionYear) {
        this.power = power;
        this.modelName = modelName;
        this.productionYear = productionYear;
    }

    public int getPower() {
        return power;
    }

    public String getModelName() {
        return modelName;
    }

    public int getProductionYear() {
        return productionYear;
    }

    @Override
    public String toString() {
        return "Car{" +
                "power=" + power +
                ", modelName='" + modelName + '\'' +
                ", productionYear=" + productionYear +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return power == car.power && productionYear == car.productionYear && Objects.equals(modelName, car.modelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(power, modelName, productionYear);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private int power;
        private String modelName;
        private int productionYear;

        @JsonProperty("power")
        public Builder power(int power) {
            if (power <= 0) {
                throw new IllegalArgumentException("Power must be greater than zero.");
            }

            this.power = power;
            return this;
        }

        @JsonProperty("modelName")
        public Builder modelName(String modelName) {
            if (modelName == null || modelName.isBlank()) {
                throw new IllegalArgumentException("Model name must be specified.");
            }

            this.modelName = modelName.trim();
            return this;
        }

        @JsonProperty("productionYear")
        public Builder productionYear(int productionYear) {
            int currentYear = Year.now().getValue();
            if (productionYear < 1886 || productionYear > currentYear) {
                throw new IllegalArgumentException("Production year must be correct.");
            }

            this.productionYear = productionYear;
            return this;
        }

        public Car build() {
            if (power <= 0) {
                throw new IllegalArgumentException("Power must be greater than zero.");
            }

            if (modelName == null || modelName.isBlank()) {
                throw new IllegalArgumentException("Model name must be specified.");
            }

            int currentYear = Year.now().getValue();
            if (productionYear < 1886 || productionYear > currentYear) {
                throw new IllegalArgumentException("Production year must be correct.");
            }

            return new Car(power, modelName, productionYear);
        }
    }
}
