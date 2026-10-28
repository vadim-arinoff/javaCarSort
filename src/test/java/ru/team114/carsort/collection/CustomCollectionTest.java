
package ru.team114.carsort.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomCollectionTest {

    @Test
    void addElements() {
        CustomCollection<String> cars = new CustomCollection<>(2);

        cars.add("Toyota");
        cars.add("BMW");
        cars.add("Audi");

        assertEquals(3, cars.size());
        assertEquals("Toyota", cars.get(0));
        assertEquals("BMW", cars.get(1));
        assertEquals("Audi", cars.get(2));
    }

    @Test
    void setElement() {
        CustomCollection<String> cars = new CustomCollection<>();
        cars.add("BMW");

        String oldValue = cars.set(0, "Mercedes");

        assertEquals("BMW", oldValue);
        assertEquals("Mercedes", cars.get(0));
    }

    @Test
    void getElementWithInvalidIndex() {
        CustomCollection<String> cars = new CustomCollection<>();

        assertThrows(IndexOutOfBoundsException.class,
                () -> cars.get(0));
    }

    @Test
    void collectionIsEmptyAtCreation() {
        CustomCollection<String> cars = new CustomCollection<>();

        assertTrue(cars.isEmpty());
        assertEquals(0, cars.size());
    }

    @Test
    void negativeCapacityThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new CustomCollection<String>(-1));
    }
}
