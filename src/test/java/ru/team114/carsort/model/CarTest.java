package ru.team114.carsort.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CarTest {
    @Test
    void createCar() {
//        проверяем, что создастся корректный объект
        Car car = new Car.Builder()
                .power(42)
                .modelName("Flying Tesla")
                .productionYear(2026)
                .build();

        assertEquals(42, car.getPower());
        assertEquals("Flying Tesla", car.getModelName());
        assertEquals(2026, car.getProductionYear());
    }

    @Test
    void nonpositivePower() {
//        проверяем, что нулевая мощность выкинет ошибку
        assertThrows(IllegalArgumentException.class, () ->
                new Car.Builder()
                .power(0)
                .modelName("Flying Tesla")
                .productionYear(2026)
                .build()
        );

//        то же для отрицательной мощности
        assertThrows(IllegalArgumentException.class, () ->
                new Car.Builder()
                        .power(-42)
                        .modelName("Flying Tesla")
                        .productionYear(2026)
                        .build()
        );
    }

    @Test
    void blankModelName() {
//        проверяем, что пустое имя выкинет ошибку
        assertThrows(IllegalArgumentException.class, () ->
                new Car.Builder()
                        .power(42)
                        .modelName("   ")
                        .productionYear(2026)
                        .build()
        );
    }

    @Test
    void invalidYear() {
//        проверяем, что год больше текущего выкинет ошибку
        assertThrows(IllegalArgumentException.class, () ->
                new Car.Builder()
                        .power(42)
                        .modelName("Flying Tesla")
                        .productionYear(3026)
                        .build()
        );

//        то же про год до машин
        assertThrows(IllegalArgumentException.class, () ->
                new Car.Builder()
                        .power(42)
                        .modelName("Flying Tesla")
                        .productionYear(1526)
                        .build()
        );
    }

    @Test
    void equalCars() {
//        проверяем, что одинаковые машини действительно одинаковы
        Car car1 = new Car.Builder()
                .power(42)
                .modelName("Flying Tesla")
                .productionYear(2026)
                .build();

        Car car2 = new Car.Builder()
                .power(42)
                .modelName("Flying Tesla")
                .productionYear(2026)
                .build();

        assertEquals(car1, car2);
        assertEquals(car1.hashCode(), car2.hashCode());
    }

    @Test
    void notEqualCarsOnPower() {
//        проверяем, что машины с разной мощностью действительно разные
        Car car1 = new Car.Builder()
                .power(42)
                .modelName("Flying Tesla")
                .productionYear(2026)
                .build();

        Car car2 = new Car.Builder()
                .power(43)
                .modelName("Flying Tesla")
                .productionYear(2026)
                .build();

        assertNotEquals(car1, car2);
        assertNotEquals(car1.hashCode(), car2.hashCode());
    }

    @Test
    void notEqualCarsOnName() {
//        проверяем, что машины с разным именем действительно разные
        Car car1 = new Car.Builder()
                .power(42)
                .modelName("Flying Tesla")
                .productionYear(2026)
                .build();

        Car car2 = new Car.Builder()
                .power(42)
                .modelName("Driving Tesla")
                .productionYear(2026)
                .build();

        assertNotEquals(car1, car2);
        assertNotEquals(car1.hashCode(), car2.hashCode());
    }

    @Test
    void notEqualCarsOnYear() {
//        проверяем, что машины с разным годом действительно разные
        Car car1 = new Car.Builder()
                .power(42)
                .modelName("Flying Tesla")
                .productionYear(2026)
                .build();

        Car car2 = new Car.Builder()
                .power(42)
                .modelName("Flying Tesla")
                .productionYear(2025)
                .build();

        assertNotEquals(car1, car2);
        assertNotEquals(car1.hashCode(), car2.hashCode());
    }

    @Test
    void carNotEqualsNull() {
//        проверяем, что машинка не null
        Car car = new Car.Builder()
                .power(42)
                .modelName("Flying Tesla")
                .productionYear(2026)
                .build();

        assertNotNull(car);
    }

    @Test
    void readCarFromFile() throws IOException {
//        проверяем чтение из файла
        ObjectMapper objectMapper = new ObjectMapper();

        Car car = objectMapper.readValue(new File("src/test/resources/testData.json"), Car.class);

        assertEquals(42, car.getPower());
        assertEquals("Flying Tesla", car.getModelName());
        assertEquals(2026, car.getProductionYear());
    }

    @Test
    void readWrongCarFromFile() {
//        проверяем, что неправильный файл не считается
        ObjectMapper objectMapper = new ObjectMapper();

        assertThrows(IOException.class, () ->
                objectMapper.readValue(new File("src/test/resources/testDataWrong.json"), Car.class)
        );


    }
}
