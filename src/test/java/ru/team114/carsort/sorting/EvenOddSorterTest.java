package ru.team114.carsort.sorting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.util.Comparator;
import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.*;

class EvenOddSorterTest {

    private CustomCollection<Car> cars;
    private ToIntFunction<Car> powerExtractor;
    private Comparator<Car> powerComparator;

    @BeforeEach
    void setUp() {
        cars = new CustomCollection<>();
        powerExtractor = Car::getPower;
        powerComparator = Comparator.comparingInt(Car::getPower);
    }

    // Хелпер для быстрого создания машин через Builder
    private Car createCar(int power, String modelName, int productionYear) {
        return new Car.Builder()
                .power(power)
                .modelName(modelName)
                .productionYear(productionYear)
                .build();
    }


    @Test
    void testOnlyOddPowers_ShouldNotChangeOrder() {
        // Только нечетная мощность
        cars.add(createCar(301, "Lada", 2010));
        cars.add(createCar(201, "Toyota", 2020));
        cars.add(createCar(101, "Kia", 2015));
        cars.add(createCar(55, "Ford", 2018));

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(powerExtractor);
        sorter.sort(cars, powerComparator);

        assertEquals(301, cars.get(0).getPower());
        assertEquals(201, cars.get(1).getPower());
        assertEquals(101, cars.get(2).getPower());
        assertEquals(55, cars.get(3).getPower());
    }

    @Test
    void testOnlyEvenPowers_ShouldFullySort() {
        //Только чётная мощность
        cars.add(createCar(200, "Lada", 2010));
        cars.add(createCar(100, "Toyota", 2020));
        cars.add(createCar(300, "Kia", 2015));
        cars.add(createCar(50, "Ford", 2018));

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(powerExtractor);
        sorter.sort(cars, powerComparator);

        assertEquals(50, cars.get(0).getPower());
        assertEquals(100, cars.get(1).getPower());
        assertEquals(200, cars.get(2).getPower());
        assertEquals(300, cars.get(3).getPower());
    }

    @Test
    void testMixedPowers_EvenSortedOddStayed() {
        // Смешанны чётные-нечетные
        cars.add(createCar(200, "Lada", 2010));
        cars.add(createCar(155, "Toyota", 2020));
        cars.add(createCar(100, "Kia", 2015));
        cars.add(createCar(99, "Ford", 2018));
        cars.add(createCar(300, "Mazda", 2019));

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(powerExtractor);
        sorter.sort(cars, powerComparator);

        assertEquals(100, cars.get(0).getPower());
        assertEquals(155, cars.get(1).getPower());
        assertEquals(200, cars.get(2).getPower());
        assertEquals(99,  cars.get(3).getPower());
        assertEquals(300, cars.get(4).getPower());

        assertEquals("Kia", cars.get(0).getModelName());
        assertEquals("Toyota", cars.get(1).getModelName());
        assertEquals("Lada", cars.get(2).getModelName());
        assertEquals("Ford", cars.get(3).getModelName());
        assertEquals("Mazda", cars.get(4).getModelName());
    }

    @Test
    void testDescendingPowerComparator() {
        //Обратный компаратор
        cars.add(createCar(200, "Lada", 2010));
        cars.add(createCar(155, "Toyota", 2020));
        cars.add(createCar(100, "Kia", 2015));
        cars.add(createCar(99, "Ford", 2018));
        cars.add(createCar(300, "Mazda", 2019));

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(powerExtractor);
        // Компаратор по УБЫВАНИЮ мощности
        sorter.sort(cars, Comparator.comparingInt(Car::getPower).reversed());

        assertEquals(300, cars.get(0).getPower());
        assertEquals(155, cars.get(1).getPower());
        assertEquals(200, cars.get(2).getPower());
        assertEquals(99,  cars.get(3).getPower());
        assertEquals(100, cars.get(4).getPower());
    }

    @Test
    void testExtractorAndComparatorUseDifferentFields() {
        // Проверяем чётность по power, а сортируем по modelName (по алфавиту)
        cars.add(createCar(200, "Zhiguli", 2010));
        cars.add(createCar(155, "Toyota",  2020));
        cars.add(createCar(100, "Audi",    2015));
        cars.add(createCar(99,  "Ford",    2018));
        cars.add(createCar(300, "BMW",     2019));

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(powerExtractor);
        Comparator<Car> nameComparator = Comparator.comparing(Car::getModelName);
        sorter.sort(cars, nameComparator);

        assertEquals("Audi",    cars.get(0).getModelName());
        assertEquals("Toyota",  cars.get(1).getModelName());
        assertEquals("BMW",     cars.get(2).getModelName());
        assertEquals("Ford",    cars.get(3).getModelName());
        assertEquals("Zhiguli", cars.get(4).getModelName());
    }


    @Test
    void testEvenOddOnProductionYear() {
        // Чётность определяется по году, а не по мощности
        cars.add(createCar(250, "A", 2020));
        cars.add(createCar(255, "B", 2021));
        cars.add(createCar(100, "C", 2018));
        cars.add(createCar(99,  "D", 2019));
        cars.add(createCar(300, "E", 2022));

        ToIntFunction<Car> yearExtractor = Car::getProductionYear;
        Comparator<Car> yearComparator = Comparator.comparingInt(Car::getProductionYear);

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(yearExtractor);
        sorter.sort(cars, yearComparator);


        assertEquals(2018, cars.get(0).getProductionYear());
        assertEquals(2021, cars.get(1).getProductionYear());
        assertEquals(2020, cars.get(2).getProductionYear());
        assertEquals(2019, cars.get(3).getProductionYear());
        assertEquals(2022, cars.get(4).getProductionYear());
    }

    @Test
    void testOnlyOneEvenElement_ShouldNotChangeOrder() {
        //Только одна чётная мощность
        cars.add(createCar(155, "A", 2010));
        cars.add(createCar(200, "B", 2020));
        cars.add(createCar(99,  "C", 2015));

        EvenOddSorter<Car> sorter = new EvenOddSorter<>(powerExtractor);
        sorter.sort(cars, powerComparator);

        assertEquals(155, cars.get(0).getPower());
        assertEquals(200, cars.get(1).getPower());
        assertEquals(99,  cars.get(2).getPower());
    }
}