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
                    "Мощность должна быть положительным числом. Получено: " + power);
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Модель не может быть пустой или null.");
        }        int currentYear = 2026;
        if (year < 1886 || year > currentYear) {
            throw new IllegalArgumentException(
                    String.format("Год выпуска должен быть от 1886 до %d. Получено: %d",
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
        return String.format("%s (%d) — %d л.с.", model, year, power);
    }
}