package ru.team114.carsort.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InputTest {

    @Mock
    private Scanner scanner;

    private ObjectMapper mapper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper();
    }

    // FileReader Tests
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
        
        ObjectMapper mapper = new ObjectMapper();
        FileReader reader = new FileReader(absolutePath, mapper, 5);

        CustomCollection<Car> result = reader.read();

        assertNotNull(result, "Коллекция не должна быть null");
        assertEquals(5, result.size(), "Должно быть прочитано ровно 5 автомобилей");

        Car car1 = result.get(0);
        assertEquals("Toyota Camry", car1.getModelName());
        assertEquals(200, car1.getPower());
        assertEquals(2022, car1.getProductionYear());

        Car car2 = result.get(1);
        assertEquals("Audi R8", car2.getModelName());
        assertEquals(525, car2.getPower());
        assertEquals(2010, car2.getProductionYear());

        Car car3 = result.get(2);
        assertEquals("Corvette C6", car3.getModelName());
        assertEquals(405, car3.getPower());
        assertEquals(2006, car3.getProductionYear());

        Car car4 = result.get(3);
        assertEquals("Honda Accord Gen 6", car4.getModelName());
        assertEquals(140, car4.getPower());
        assertEquals(1998, car4.getProductionYear());
        
        Car car5 = result.get(4);
        assertEquals("Toyota Mark II", car5.getModelName());
        assertEquals(200, car5.getPower());
        assertEquals(2001, car5.getProductionYear());
    }

    @Test
    @DisplayName("FileReader: Чтение ровно N машин, игнорируя лишние")
    void testFileReaderIgnoresExtraCarsInFile() throws IOException {
        InputStream resourceStream = getClass().getResourceAsStream("/test_car.json");
        assertNotNull(resourceStream, "Ресурс test_car.json должен существовать в src/test/resources");
        
        String jsonContent = new String(resourceStream.readAllBytes());
        resourceStream.close();
        
        Path tempFile = tempDir.resolve("test_car.json");
        Files.writeString(tempFile, jsonContent);
        String absolutePath = tempFile.toAbsolutePath().toString();
        
        ObjectMapper mapper = new ObjectMapper();
        
        FileReader reader = new FileReader(absolutePath, mapper, 5);
        CustomCollection<Car> result = reader.read();

        assertNotNull(result, "Коллекция не должна быть null");
        assertEquals(5, result.size(), "Должно быть прочитано ровно 5 автомобилей, остальные 3 должны быть проигнорированы");
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            result.get(5);
        }, "Попытка получить 6-й автомобиль должна вызвать ошибку, так как запрошено и загружено было только 5");
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

    // ResourceReader Tests
    @Test
    @DisplayName("ResourceReader: Ошибка при отсутствии ресурса в classpath")
    void testResourceReaderNotFound() {
        ResourceReader reader = new ResourceReader("non_existent_resource.json", mapper);
        RuntimeException exception = assertThrows(RuntimeException.class, reader::read);
        assertTrue(exception.getMessage().contains("Resource not found in classpath"));
    }

    @Test
    @DisplayName("ResourceReader: Успешное чтение test_car.json из classpath")
    void testResourceReaderSuccess() {
        ObjectMapper mapper = new ObjectMapper();
        String resourceName = "test_car.json"; 
        ResourceReader reader = new ResourceReader(resourceName, mapper);

        CustomCollection<Car> result = reader.read();

        assertNotNull(result, "Коллекция не должна быть null");
        assertEquals(8, result.size(), "Должны быть прочитаны все машины test_car.json (8)");

        Car car1 = result.get(0);
        assertEquals("Toyota Camry", car1.getModelName());
        assertEquals(200, car1.getPower());
        assertEquals(2022, car1.getProductionYear());

        Car car2 = result.get(1);
        assertEquals("Audi R8", car2.getModelName());
        assertEquals(525, car2.getPower());
        assertEquals(2010, car2.getProductionYear());

        Car car3 = result.get(2);
        assertEquals("Corvette C6", car3.getModelName());
        assertEquals(405, car3.getPower());
        assertEquals(2006, car3.getProductionYear());

        Car car4 = result.get(3);
        assertEquals("Honda Accord Gen 6", car4.getModelName());
        assertEquals(140, car4.getPower());
        assertEquals(1998, car4.getProductionYear());
        
        Car car5 = result.get(4);
        assertEquals("Toyota Mark II", car5.getModelName());
        assertEquals(200, car5.getPower());
        assertEquals(2001, car5.getProductionYear());
    }

    // RandomReader Tests
    @Test
    @DisplayName("RandomReader: Успешная генерация заданного количества машин")
    void testRandomReaderSuccess() {
        when(scanner.nextLine()).thenReturn("3");
        
        RandomReader reader = new RandomReader(scanner);
        CustomCollection<Car> result = reader.read();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("RandomReader: Обработка невалидного ввода и последующий успех")
    void testRandomReaderInvalidThenValidInput() {
        when(scanner.nextLine())
                .thenReturn("abc")   // Не число
                .thenReturn("-5")    // Отрицательное
                .thenReturn("0")     // Ноль
                .thenReturn("2");    // Валидное

        RandomReader reader = new RandomReader(scanner);
        CustomCollection<Car> result = reader.read();

        assertEquals(2, result.size());
        verify(scanner, times(4)).nextLine();
    }

    // ConsoleReader Tests
    @Test
    @DisplayName("ConsoleReader: Успешный парсинг JSON из первой строки")
    void testConsoleReaderJsonSuccess() {
        String jsonInput = "[{\"modelName\":\"BMW\",\"power\":250,\"productionYear\":2022}]";
        ConsoleReader reader = new ConsoleReader(scanner, jsonInput, mapper);
        
        CustomCollection<Car> result = reader.read();
        
        assertEquals(1, result.size());
        verify(scanner, never()).nextLine();
    }

    @Test
    @DisplayName("ConsoleReader: Fallback на интерактивный ввод при невалидном JSON")
    void testConsoleReaderFallbackToInteractive() {
        // firstLine не является валидным JSON, поэтому сработает fallback
        ConsoleReader reader = new ConsoleReader(scanner, "Audi", mapper);

        // Эмуляция ответов пользователя в ИНТЕРАКТИВНОМ режиме (targetCount = 0)
        when(scanner.nextLine())
                .thenReturn("0")       // targetCount = 0 (включаем бесконечный интерактивный режим)
                .thenReturn("150")     // power
                .thenReturn("2015")    // year
                .thenReturn("n");      // добавить еще? (это прервет цикл)

        CustomCollection<Car> result = reader.read();

        assertEquals(1, result.size());
        assertEquals("Audi", result.get(0).getModelName());
        
        verify(scanner, times(4)).nextLine();
    }

    @Test
    @DisplayName("ConsoleReader: Валидация года и мощности в интерактивном режиме")
    void testConsoleReaderValidation() {
        ConsoleReader reader = new ConsoleReader(scanner, "", mapper);

        when(scanner.nextLine())
                .thenReturn("1")        // targetCount
                .thenReturn("MyCar")    // model
                .thenReturn("-10")      // invalid power
                .thenReturn("120")      // valid power
                .thenReturn("1800")     // invalid year (< 1886)
                .thenReturn("3000")     // invalid year (> current)
                .thenReturn("2020")     // valid year
                .thenReturn("n");       // keep adding

        CustomCollection<Car> result = reader.read();

        assertEquals(1, result.size());
    }

    // Input Tests
    @Test
    @DisplayName("Input: Маршрутизация на RandomReader")
    void testInputResolveRandom() {
        when(scanner.nextLine()).thenReturn("random").thenReturn("1");
        Input input = new Input(scanner, mapper);
        
        CustomCollection<Car> result = input.execute();
        
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Input: Маршрутизация на FileReader (абсолютный путь)")
    void testInputResolveAbsoluteFile() throws IOException {
        Path filePath = tempDir.resolve("test.json");
        Files.writeString(filePath, "[{\"modelName\":\"Ford\",\"power\":100,\"productionYear\":2010}]");
        
        when(scanner.nextLine())
                .thenReturn(filePath.toString())
                .thenReturn("1"); 
                
        Input input = new Input(scanner, mapper);
        
        CustomCollection<Car> result = input.execute();
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Input: Маршрутизация на ResourceReader (относительный .json)")
    void testInputResolveResource() {
        when(scanner.nextLine()).thenReturn("cars.json");
        Input input = new Input(scanner, mapper);
        
        RuntimeException exception = assertThrows(RuntimeException.class, input::execute);
        assertTrue(exception.getMessage().contains("Resource not found in classpath"));
    }

    @Test
    @DisplayName("Input: Пустой ввод вызывает IllegalArgumentException")
    void testInputEmptyString() {
        when(scanner.nextLine()).thenReturn("   ");
        Input input = new Input(scanner, mapper);
        
        assertThrows(IllegalArgumentException.class, input::execute);
    }
}