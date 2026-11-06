package ru.team114.carsort.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;
import ru.team114.carsort.output.FileOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutputTest {

    private FileOutput fileOutput;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileOutput = new FileOutput();
    }

    private CustomCollection<Car> createTestCollection() {
        CustomCollection<Car> collection = new CustomCollection<>();
        collection.add(new Car.Builder()
                .modelName("TestCar")
                .power(150)
                .productionYear(2023)
                .build());
        return collection;
    }

    @Test
    @DisplayName("FileOutput: Успешная запись в новый файл с созданием директорий")
    void testWriteNewFileWithDirectories() throws IOException {
        Path targetPath = tempDir.resolve("subdir/nested/cars.json");
        CustomCollection<Car> data = createTestCollection();

        fileOutput.write(data, targetPath.toString());

        assertTrue(Files.exists(targetPath));
        List<String> lines = Files.readAllLines(targetPath);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("TestCar"));
    }

    @Test
    @DisplayName("FileOutput: Дописывание (APPEND) в существующий файл")
    void testWriteAppendToExistingFile() throws IOException {
        Path targetPath = tempDir.resolve("append_test.json");
        Files.writeString(targetPath, "{\"modelName\":\"OldCar\",\"power\":100,\"productionYear\":2000}\n");

        CustomCollection<Car> data = createTestCollection();
        fileOutput.write(data, targetPath.toString());

        List<String> lines = Files.readAllLines(targetPath);
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("TestCar"));
    }

    @Test
    @DisplayName("FileOutput: Игнорирование null или пустой коллекции")
    void testWriteNullOrEmpty() {
        Path targetPath = tempDir.resolve("empty.json");

        assertDoesNotThrow(() -> fileOutput.write(null, targetPath.toString()));
        assertFalse(Files.exists(targetPath));

        CustomCollection<Car> emptyCollection = new CustomCollection<>();
        assertDoesNotThrow(() -> fileOutput.write(emptyCollection, targetPath.toString()));
        assertFalse(Files.exists(targetPath));
    }

    @Test
    @DisplayName("FileOutput: writeSingle успешно делегирует методу write")
    void testWriteSingle() throws IOException {
        Path targetPath = tempDir.resolve("single.json");
        Car singleCar = new Car.Builder()
                .modelName("SingleCar")
                .power(200)
                .productionYear(2021)
                .build();

        fileOutput.writeSingle(singleCar, targetPath.toString());

        assertTrue(Files.exists(targetPath));
        List<String> lines = Files.readAllLines(targetPath);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("SingleCar"));
    }
}