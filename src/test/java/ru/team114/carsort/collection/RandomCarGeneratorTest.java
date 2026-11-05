package ru.team114.carsort.collection;

import org.junit.jupiter.api.Test;
import ru.team114.carsort.model.Car;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class RandomCarGeneratorTest {

    private final RandomCarGenerator generator = new RandomCarGenerator();

    @Test
    void generateShouldReturnRequestedNumberOfCars() {
        CustomCollection<Car> cars = generator.generate(10);

        assertEquals(10, cars.size());
    }

    @Test
    void generateShouldReturnEmptyCollectionWhenCountIsZero() {
        CustomCollection<Car> cars = generator.generate(0);

        assertNotNull(cars);
        assertTrue(cars.isEmpty());
    }

    @Test
    void generateShouldThrowExceptionWhenCountIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(-1)
        );
    }

    @Test
    void generateShouldCreateCarsWithValidProperties() {
        CustomCollection<Car> cars = generator.generate(100);
        int currentYear = Year.now().getValue();

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);

            assertNotNull(car);
            assertTrue(car.getPower() > 0);
            assertNotNull(car.getModelName());
            assertFalse(car.getModelName().isBlank());
            assertTrue(car.getProductionYear() >= 1886);
            assertTrue(car.getProductionYear() <= currentYear);
        }
    }
}