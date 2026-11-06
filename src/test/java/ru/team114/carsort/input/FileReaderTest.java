package ru.team114.carsort.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderTest {

    private ObjectMapper mapper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    @DisplayName("FileReader: Успешное чтение test_car.json по полному пути")
    void testFileReaderSuccess() throws IOException {
        InputStream resourceStream = getClass().getResourceAsStream("/test_car.json");
        assertNotNull(resourceStream, "Ресурс test_car.json должен существовать в src/test/resources");

        String jsonContent = new String(resourceStream.readAllBytes());
        resourceStream.close();

        Path tempFile = tempDir.resolve("test_car.json");
        Files.writeString(tempFile, jsonContent);
        String absolutePath = tempFile.toAbsolutePath().toString();

        FileReader reader = new FileReader(absolutePath, mapper, 5);
        CustomCollection<Car> result = reader.read();

        assertNotNull(result, "Коллекция не должна быть null");
        assertEquals(5, result.size(), "Должно быть прочитано ровно 5 автомобилей");

        assertEquals("Toyota Camry", result.get(0).getModelName());
        assertEquals("Audi R8", result.get(1).getModelName());
        assertEquals("Corvette C6", result.get(2).getModelName());
        assertEquals("Honda Accord Gen 6", result.get(3).getModelName());
        assertEquals("Toyota Mark II", result.get(4).getModelName());
    }

    @Test
    @DisplayName("FileReader: Чтение ровно N машин, игнорируя лишние")
    void testFileReaderIgnoresExtraCarsInFile() throws IOException {
        InputStream resourceStream = getClass().getResourceAsStream("/test_car.json");
        assertNotNull(resourceStream, "Ресурс test_car.json должен существовать в src/test/resources");

        Path tempFile = tempDir.resolve("test_car.json");
        Files.writeString(tempFile, new String(resourceStream.readAllBytes()));
        resourceStream.close();

        FileReader reader = new FileReader(tempFile.toAbsolutePath().toString(), mapper, 5);
        CustomCollection<Car> result = reader.read();

        assertEquals(5, result.size(), "Должно быть прочитано ровно 5 автомобилей, остальные проигнорированы");
        assertThrows(IndexOutOfBoundsException.class, () -> result.get(5),
                "Попытка получить 6-й автомобиль должна вызвать ошибку");
    }

    @Test
    @DisplayName("FileReader: Ошибка, если в файле машин меньше, чем запрошено (n)")
    void testFileReaderFewerCarsThanN() throws IOException {
        Path tempFile = tempDir.resolve("few_cars.json");
        String jsonWithTwoCars = """
                [
                  {"modelName": "Car1", "power": 100, "productionYear": 2020},
                  {"modelName": "Car2", "power": 150, "productionYear": 2021}
                ]
                """;
        Files.writeString(tempFile, jsonWithTwoCars);

        FileReader reader = new FileReader(tempFile.toString(), mapper, 5);

        RuntimeException exception = assertThrows(RuntimeException.class, reader::read);
        assertTrue(exception.getMessage().contains("меньше запрошенных"));
        assertTrue(exception.getMessage().contains("Частичная коллекция не возвращается"));
    }

    @Test
    @DisplayName("FileReader: Ошибка при отсутствии файла")
    void testFileReaderFileNotFound() {
        FileReader reader = new FileReader("non_existent_file.json", mapper, 5);
        RuntimeException exception = assertThrows(RuntimeException.class, reader::read);
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    @DisplayName("FileReader: Ошибка при невалидном JSON")
    void testFileReaderInvalidJson() throws IOException {
        Path filePath = tempDir.resolve("invalid.json");
        Files.writeString(filePath, "not a valid json");

        FileReader reader = new FileReader(filePath.toString(), mapper, 5);
        RuntimeException exception = assertThrows(RuntimeException.class, reader::read);
        assertTrue(exception.getMessage().contains("Error reading file"));
    }
}