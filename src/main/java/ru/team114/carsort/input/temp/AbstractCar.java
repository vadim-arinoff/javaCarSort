package ru.team114.carsort.input.temp;

public abstract class AbstractCar {
    protected final int power;
    protected final String model;
    protected final int year;

    protected AbstractCar(int power, String model, int year) {
        validate(power, model, year);
        this.power = power;
        this.model = model;
        this.year = year;
    }

    private void validate(int power, String model, int year) {
        if (power <= 0) {
            throw new IllegalArgumentException(
                    "Power must be a positive number. Received: " + power);
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be empty or null.");
        }
        int currentYear = 2026;
        if (year < 1886 || year > currentYear) {
            throw new IllegalArgumentException(
                    String.format("Year of manufacture must be between 1886 and %d. Received: %d",
                            currentYear, year));
        }
    }

    public int getPower() {
        return power;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return String.format("%s (%d) — %d hp", model, year, power);
    }
}