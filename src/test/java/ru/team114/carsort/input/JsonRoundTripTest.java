package ru.team114.carsort.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;
import ru.team114.carsort.output.FileOutput;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonRoundTripTest {

    @TempDir
    Path tempDir;

    @Test
    void savedCarsCanBeReadBack() {
        Path file = tempDir.resolve("results.json");
        CustomCollection<Car> original = new CustomCollection<>();

        original.add(new Car.Builder()
                .power(120)
                .modelName("Toyota")
                .productionYear(2020)
                .build());

        original.add(new Car.Builder()
                .power(95)
                .modelName("Kia")
                .productionYear(2022)
                .build());

        new FileOutput().write(original, file.toString());

        CustomCollection<Car> loaded =
                new FileReader(file.toString(), new ObjectMapper(), 2).read();

        assertEquals(2, loaded.size());
        assertEquals(original.get(0), loaded.get(0));
        assertEquals(original.get(1), loaded.get(1));
    }
}