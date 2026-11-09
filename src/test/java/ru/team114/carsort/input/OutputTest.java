package ru.team114.carsort.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper mapper = new ObjectMapper();

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
    @DisplayName("FileOutput: Успешная запись в новый файл с созданием директорий и расширением .json")
    void testWriteNewFileWithDirectories() throws IOException {
        Path requestedPath = tempDir.resolve("subdir/nested/cars.json");
        
        CustomCollection<Car> data = createTestCollection();
        fileOutput.write(data, requestedPath.toString());

        assertTrue(Files.exists(requestedPath), "Файл должен быть создан по указанному пути с .json");
        
        List<Car> savedCars = mapper.readValue(requestedPath.toFile(), new TypeReference<List<Car>>() {});
        assertEquals(1, savedCars.size());
        assertEquals("TestCar", savedCars.get(0).getModelName());
        assertEquals(150, savedCars.get(0).getPower());
    }

    @Test
    @DisplayName("FileOutput: Логическое добавление (APPEND) в существующий JSON-файл")
    void testWriteAppendToExistingFile() throws IOException {
        Path targetPath = tempDir.resolve("append_test.json");
        
        String initialJson = "[{\"modelName\":\"OldCar\",\"power\":100,\"productionYear\":2000}]";
        Files.writeString(targetPath, initialJson);

        CustomCollection<Car> data = createTestCollection();
        fileOutput.write(data, targetPath.toString());

        assertTrue(Files.exists(targetPath));
        
        List<Car> savedCars = mapper.readValue(targetPath.toFile(), new TypeReference<List<Car>>() {});
        
        assertEquals(2, savedCars.size(), "В файле должно быть 2 автомобиля после добавления");
        assertEquals("OldCar", savedCars.get(0).getModelName());
        assertEquals("TestCar", savedCars.get(1).getModelName());
    }

    @Test
    @DisplayName("FileOutput: Игнорирование null или пустой коллекции (файл не создается)")
    void testWriteNullOrEmpty() {
        Path targetPath = tempDir.resolve("empty.json");

        assertDoesNotThrow(() -> fileOutput.write(null, targetPath.toString()));
        assertFalse(Files.exists(targetPath), "Файл не должен быть создан для null");

        CustomCollection<Car> emptyCollection = new CustomCollection<>();
        assertDoesNotThrow(() -> fileOutput.write(emptyCollection, targetPath.toString()));
        assertFalse(Files.exists(targetPath), "Файл не должен быть создан для пустой коллекции");
    }

    @Test
    @DisplayName("FileOutput: Запись коллекции из одного элемента успешно создает валидный JSON-массив")
    void testWriteSingleElementCollection() throws IOException {
        Path targetPath = tempDir.resolve("single.json"); 
        
        CustomCollection<Car> singleCarCollection = new CustomCollection<>();
        singleCarCollection.add(new Car.Builder()
                .modelName("SingleCar")
                .power(200)
                .productionYear(2021)
                .build());

        fileOutput.write(singleCarCollection, targetPath.toString());

        assertTrue(Files.exists(targetPath), "Файл должен быть сохранен с расширением .json");
        
        List<Car> savedCars = mapper.readValue(targetPath.toFile(), new TypeReference<List<Car>>() {});
        assertEquals(1, savedCars.size());
        assertEquals("SingleCar", savedCars.get(0).getModelName());
    }
    
    @Test
    @DisplayName("FileOutput: Принудительная замена расширения .jsonl на .json")
    void testForceJsonExtension() throws IOException {
        Path requestedPath = tempDir.resolve("wrong_extension.jsonl");
        Path expectedPath = tempDir.resolve("wrong_extension.json");
        
        CustomCollection<Car> data = createTestCollection();
        fileOutput.write(data, requestedPath.toString());

        assertFalse(Files.exists(requestedPath), "Файл с расширением .jsonl не должен быть создан");
        assertTrue(Files.exists(expectedPath), "Файл должен быть сохранен с принудительным расширением .json");
        
        List<Car> savedCars = mapper.readValue(expectedPath.toFile(), new TypeReference<List<Car>>() {});
        assertEquals(1, savedCars.size());
    }
}