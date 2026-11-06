package ru.team114.carsort.sorting;

import org.junit.jupiter.api.Test;
import ru.team114.carsort.model.Car;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortingStrategyTest {

    @Test
    void bubbleSortByPower() {
        TestMutableSequence<Car> cars = createCars();

        SortStrategy<Car> sort = new BubbleSortStrategy<>();
        sort.sort(cars, CarComparators.BY_POWER);

        assertEquals(150, cars.get(0).getPower());
        assertEquals(200, cars.get(1).getPower());
        assertEquals(300, cars.get(2).getPower());
    }

    @Test
    void bubbleSortByModelName() {
        TestMutableSequence<Car> cars = createCars();

        SortStrategy<Car> sort = new BubbleSortStrategy<>();
        sort.sort(cars, CarComparators.BY_MODEL_NAME);

        assertEquals("Audi", cars.get(0).getModelName());
        assertEquals("BMW", cars.get(1).getModelName());
        assertEquals("Volvo", cars.get(2).getModelName());
    }

    @Test
    void bubbleSortByProductionYear() {
        TestMutableSequence<Car> cars = createCars();

        SortStrategy<Car> sort = new BubbleSortStrategy<>();
        sort.sort(cars, CarComparators.BY_PRODUCTION_YEAR);

        assertEquals(2020, cars.get(0).getProductionYear());
        assertEquals(2022, cars.get(1).getProductionYear());
        assertEquals(2024, cars.get(2).getProductionYear());
    }

    @Test
    void selectionSortByPower() {
        TestMutableSequence<Car> cars = createCars();

        SortStrategy<Car> sort = new SelectionSortStrategy<>();
        sort.sort(cars, CarComparators.BY_POWER);

        assertEquals(150, cars.get(0).getPower());
        assertEquals(200, cars.get(1).getPower());
        assertEquals(300, cars.get(2).getPower());
    }

    @Test
    void selectionSortByModelName() {
        TestMutableSequence<Car> cars = createCars();

        SortStrategy<Car> sort = new SelectionSortStrategy<>();
        sort.sort(cars, CarComparators.BY_MODEL_NAME);

        assertEquals("Audi", cars.get(0).getModelName());
        assertEquals("BMW", cars.get(1).getModelName());
        assertEquals("Volvo", cars.get(2).getModelName());
    }

    @Test
    void selectionSortByProductionYear() {
        TestMutableSequence<Car> cars = createCars();

        SortStrategy<Car> sort = new SelectionSortStrategy<>();
        sort.sort(cars, CarComparators.BY_PRODUCTION_YEAR);

        assertEquals(2020, cars.get(0).getProductionYear());
        assertEquals(2022, cars.get(1).getProductionYear());
        assertEquals(2024, cars.get(2).getProductionYear());
    }

    private TestMutableSequence<Car> createCars() {
        TestMutableSequence<Car> cars = new TestMutableSequence<>();

        cars.add(new Car.Builder()
                .power(300)
                .modelName("BMW")
                .productionYear(2022)
                .build());

        cars.add(new Car.Builder()
                .power(150)
                .modelName("Audi")
                .productionYear(2020)
                .build());

        cars.add(new Car.Builder()
                .power(200)
                .modelName("Volvo")
                .productionYear(2024)
                .build());

        return cars;
    }
}