package ru.team114.carsort.multithreading;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 5, unit = TimeUnit.SECONDS)
class CarCounterTest {

    private CustomCollection<Car> cars;
    private Car targetCar;

    // Инструменты для перехвата консоли
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Перенаправляет вывод в буфер перед каждым тестом
        System.setOut(new PrintStream(outputStreamCaptor));

        cars = new CustomCollection<>();
        targetCar = createCar(150, "Lada", 2010);
    }

    @AfterEach
    void tearDown() {
        // Возвращает консоль на место после каждого теста
        System.setOut(originalOut);
    }

    // Вспомогательный метод для создания объекта
    private Car createCar(int power, String modelName, int productionYear) {
        return new Car.Builder()
                .power(power)
                .modelName(modelName)
                .productionYear(productionYear)
                .build();
    }

    // Вспомогательный метод для получения последней строки из консоли
    private String getConsoleOutput() {
        return outputStreamCaptor.toString().trim();
    }

    @Test
    void testNullCollection_ShouldThrowException() {
        // Проверка на null
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CarCounter.countOccurrences(null, targetCar)
        );
        assertEquals("Collection cannot be null", exception.getMessage());
    }

    @Test
    void testEmptyCollection_ShouldPrintZero() {
        // Проверка пустой коллекции
        CarCounter.countOccurrences(cars, targetCar);

        // Проверка, что в консоль вывелся правильный текст
        assertEquals("Количество вхождений элемента: 0", getConsoleOutput());
    }

    @Test
    void testNoMatches_ShouldPrintZero() {
        // Проверка если 0 совпадений
        cars.add(createCar(200, "Toyota", 2020));
        cars.add(createCar(100, "Kia", 2015));

        CarCounter.countOccurrences(cars, targetCar);

        assertEquals("Количество вхождений элемента: 0", getConsoleOutput());
    }

    @Test
    void testAllElementsMatch_ShouldPrintSize() {
        // Проверка, что элементы не теряются
        for (int i = 0; i < 5; i++) {
            cars.add(createCar(150, "Lada", 2010));
        }

        CarCounter.countOccurrences(cars, targetCar);

        assertEquals("Количество вхождений элемента: 5", getConsoleOutput());
    }

    // Проверка базовой логики
    @Test
    void testPartialMatches_ShouldPrintCorrectCount() {
        // Проверка базовой логики
        cars.add(createCar(150, "Lada", 2010));
        cars.add(createCar(200, "Toyota", 2020));
        cars.add(createCar(150, "Lada", 2010));
        cars.add(createCar(100, "Kia", 2015));
        cars.add(createCar(150, "Lada", 2010));

        CarCounter.countOccurrences(cars, targetCar);

        assertEquals("Количество вхождений элемента: 3", getConsoleOutput());
    }

    @Test
    void testSizeNotDivisibleByThreadCount_ShouldNotLoseElements() {
        // Проверка распределения остатка
        for (int i = 0; i < 11; i++) {
            cars.add(createCar(150, "Lada", 2010));
        }

        CarCounter.countOccurrences(cars, targetCar);

        assertEquals("Количество вхождений элемента: 11", getConsoleOutput());
    }


    @Test
    void testNullTargetElement() {
        // Проверка NullPointerException
        CustomCollection<Car> nullCars = new CustomCollection<>();
        nullCars.add(null);
        nullCars.add(createCar(150, "Lada", 2010));
        nullCars.add(null);

        CarCounter.countOccurrences(nullCars, null);

        assertEquals("Количество вхождений элемента: 2", getConsoleOutput());
    }
}