package ru.team114.carsort.sorting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class EvenOddSorterTest {

    private CustomCollection<Car> cars;
    private Comparator<Car> powerComparator;
    private SortStrategy<Car> innerStrategy;

    @BeforeEach
    void setUp() {
        cars = new CustomCollection<>();
        powerComparator = Comparator.comparingInt(Car::getPower);

        innerStrategy = new BubbleSortStrategy<>();
    }


    private Car createCar(int power, String modelName, int productionYear) {
        return new Car.Builder()
                .power(power)
                .modelName(modelName)
                .productionYear(productionYear)
                .build();
    }


    @Test
    void testConstructor_NullExtractor_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new EvenOddSorter<Car>(null, innerStrategy)
        );
        assertEquals("Numeric field extractor cannot be null", ex.getMessage());
    }

    @Test
    void testConstructor_NullInnerStrategy_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new EvenOddSorter<Car>(Car::getPower, null)
        );
        assertEquals("Inner sorting strategy cannot be null", ex.getMessage());
    }


    @Test
    void testSort_NullCollection_ShouldThrowException() {
        EvenOddSorter<Car> sorter = new EvenOddSorter<>(Car::getPower, innerStrategy);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sorter.sort(null, powerComparator)
        );
        assertEquals("Elements collection cannot be null", ex.getMessage());
    }

    @Test
    void testSort_NullComparator_ShouldThrowException() {
        EvenOddSorter<Car> sorter = new EvenOddSorter<>(Car::getPower, innerStrategy);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sorter.sort(cars, null)
        );
        assertEquals("Comparator cannot be null", ex.getMessage());
    }


    @Test
    void testOnlyOddPowers_ShouldNotChangeOrder() {
        cars.add(createCar(301, "Lada", 2010));
        cars.add(createCar(201, "Toyota", 2020));
        cars.add(createCar(101, "Kia", 2015));
        cars.add(createCar(55, "Ford", 2018));

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(Car::getPower, innerStrategy);
        sorter.sort(cars, powerComparator);


        assertEquals(301, cars.get(0).getPower());
        assertEquals(201, cars.get(1).getPower());
        assertEquals(101, cars.get(2).getPower());
        assertEquals(55, cars.get(3).getPower());
    }

    @Test
    void testOnlyEvenPowers_ShouldFullySort() {
        cars.add(createCar(200, "Lada", 2010));
        cars.add(createCar(100, "Toyota", 2020));
        cars.add(createCar(300, "Kia", 2015));
        cars.add(createCar(50, "Ford", 2018));

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(Car::getPower, innerStrategy);
        sorter.sort(cars, powerComparator);


        assertEquals(50, cars.get(0).getPower());
        assertEquals(100, cars.get(1).getPower());
        assertEquals(200, cars.get(2).getPower());
        assertEquals(300, cars.get(3).getPower());
    }

    @Test
    void testMixedPowers_EvenSortedOddStayed() {
        cars.add(createCar(200, "Lada", 2010));
        cars.add(createCar(155, "Toyota", 2020));
        cars.add(createCar(100, "Kia", 2015));
        cars.add(createCar(99, "Ford", 2018));
        cars.add(createCar(300, "Mazda", 2019));

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(Car::getPower, innerStrategy);
        sorter.sort(cars, powerComparator);

        assertEquals(100, cars.get(0).getPower());
        assertEquals(155, cars.get(1).getPower());
        assertEquals(200, cars.get(2).getPower());
        assertEquals(99, cars.get(3).getPower());
        assertEquals(300, cars.get(4).getPower());

        assertEquals("Kia", cars.get(0).getModelName());
        assertEquals("Toyota", cars.get(1).getModelName());
        assertEquals("Lada", cars.get(2).getModelName());
        assertEquals("Ford", cars.get(3).getModelName());
        assertEquals("Mazda", cars.get(4).getModelName());
    }

    @Test
    void testEvenOddOnProductionYear() {
        cars.add(createCar(250, "A", 2020));
        cars.add(createCar(255, "B", 2021));
        cars.add(createCar(100, "C", 2018));
        cars.add(createCar(99, "D", 2019));
        cars.add(createCar(300, "E", 2022));

        Comparator<Car> yearComparator = Comparator.comparingInt(Car::getProductionYear);

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(Car::getProductionYear, innerStrategy);
        sorter.sort(cars, yearComparator);

        assertEquals(2018, cars.get(0).getProductionYear());
        assertEquals(2021, cars.get(1).getProductionYear());
        assertEquals(2020, cars.get(2).getProductionYear());
        assertEquals(2019, cars.get(3).getProductionYear());
        assertEquals(2022, cars.get(4).getProductionYear());
    }
}