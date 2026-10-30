
package ru.team114.carsort.collection;

import org.junit.jupiter.api.Test;
import ru.team114.carsort.model.Car;

import static org.junit.jupiter.api.Assertions.*;

class CustomCollectionTest {

    @Test
    void addShouldIncreaseSizeAndStoreElements() {
        CustomCollection<String> collection = new CustomCollection<>();

        collection.add("First");
        collection.add("Second");

        assertEquals(2, collection.size());
        assertEquals("First", collection.get(0));
        assertEquals("Second", collection.get(1));
    }

    @Test
    void addShouldIncreaseCapacityWhenCollectionIsFull() {
        CustomCollection<Integer> collection = new CustomCollection<>(1);

        collection.add(10);
        collection.add(20);

        assertEquals(2, collection.size());
        assertEquals(10, collection.get(0));
        assertEquals(20, collection.get(1));
    }

    @Test
    void setShouldReplaceElementAndReturnOldElement() {
        CustomCollection<String> collection = new CustomCollection<>();

        collection.add("Old");
        String oldElement = collection.set(0, "New");

        assertEquals("Old", oldElement);
        assertEquals("New", collection.get(0));
    }

    @Test
    void getShouldThrowExceptionWhenIndexIsInvalid() {
        CustomCollection<String> collection = new CustomCollection<>();

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> collection.get(0)
        );
    }

    @Test
    void collectionShouldBeEmptyWhenCreated() {
        CustomCollection<String> collection = new CustomCollection<>();

        assertTrue(collection.isEmpty());
        assertEquals(0, collection.size());
    }

    @Test
    void constructorShouldThrowExceptionWhenCapacityIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CustomCollection<>(-1)
        );
    }

    @Test
    void addShouldStoreCarObjects() {
        CustomCollection<Car> cars = new CustomCollection<>();

        Car car = new Car.Builder()
                .power(150)
                .modelName("Toyota Camry")
                .productionYear(2020)
                .build();

        cars.add(car);

        assertEquals(1, cars.size());
        assertSame(car, cars.get(0));
    }

    @Test
    void setShouldReplaceCarObject() {
        CustomCollection<Car> cars = new CustomCollection<>();

        Car oldCar = new Car.Builder()
                .power(150)
                .modelName("Toyota Camry")
                .productionYear(2020)
                .build();

        Car newCar = new Car.Builder()
                .power(200)
                .modelName("BMW X5")
                .productionYear(2022)
                .build();

        cars.add(oldCar);

        Car replacedCar = cars.set(0, newCar);

        assertSame(oldCar, replacedCar);
        assertSame(newCar, cars.get(0));
    }
}
