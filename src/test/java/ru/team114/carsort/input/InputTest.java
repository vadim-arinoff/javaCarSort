package ru.team114.carsort.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.team114.carsort.model.Car;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InputTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        // Та же настройка, что и в продакшене
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    // Вспомогательный метод для создания Input с заданной строкой ввода
    private Input createInputWith(String simulatedInput) {
        Scanner scanner = new Scanner(simulatedInput);
        return new Input(scanner, mapper);
    }

    @Test
    void testReadFromResource() {
        // Имитируем ввод имени файла из classpath
        Input input = createInputWith("test_car.json");
        
        List<Car> cars = input.execute();
        
        assertNotNull(cars);
        assertEquals(2, cars.size());
        assertEquals("Toyota Camry", cars.get(0).getModelName());
        assertEquals(200, cars.get(0).getPower());
        assertEquals("Audi R8", cars.get(1).getModelName());
    }

    @Test
    void testReadFromAbsolutePath() throws Exception {
        File tempFile = File.createTempFile("cars_test", ".json");
        tempFile.deleteOnExit();
        
        String jsonContent = "[{\"modelName\": \"File Car\", \"power\": 200, \"productionYear\": 2021}]";
        Files.writeString(tempFile.toPath(), jsonContent, StandardCharsets.UTF_8);

        // Имитируем ввод абсолютного пути
        Input input = createInputWith(tempFile.getAbsolutePath());
        
        List<Car> cars = input.execute();
        
        assertNotNull(cars);
        assertEquals(1, cars.size());
        assertEquals("File Car", cars.get(0).getModelName());
        assertEquals(200, cars.get(0).getPower());
    }

    @Test
    void testConsoleInput_ValidJsonArray() {
        // Обновлено: ключи JSON изменены на modelName и productionYear
        String simulatedInput = "[{\"modelName\": \"JSON Array Car\", \"power\": 400, \"productionYear\": 2023}]";
        Input input = createInputWith(simulatedInput);
        
        List<Car> cars = input.execute();
        
        assertNotNull(cars);
        assertEquals(1, cars.size());
        assertEquals("JSON Array Car", cars.get(0).getModelName());
    }

    @Test
    void testConsoleInput_ValidSingleJsonObject() {
        // Оодиночный объект тоже должен стать списком из 1 элемента
        String simulatedInput = "{\"modelName\": \"Single JSON Car\", \"power\": 120, \"productionYear\": 2019}";
        Input input = createInputWith(simulatedInput);
        
        List<Car> cars = input.execute();
        
        assertNotNull(cars);
        assertEquals(1, cars.size());
        assertEquals("Single JSON Car", cars.get(0).getModelName());
    }

    @Test
    void testConsoleInput_ManualEntry() {
        // Пользователь вводит название, затем мощность, год, и отказывается добавлять еще ('n')
        String simulatedInput = "Manual Car\n150\n2018\nn\n";
        Input input = createInputWith(simulatedInput);
        
        List<Car> cars = input.execute();
        
        assertNotNull(cars);
        assertEquals(1, cars.size());
        assertEquals("Manual Car", cars.get(0).getModelName());
        assertEquals(150, cars.get(0).getPower());
        assertEquals(2018, cars.get(0).getProductionYear());
    }

    @Test
    void testConsoleInput_MultipleManualEntries() {
        // Сценарий: добавляем две машины вручную
        String simulatedInput = "Car One\n100\n2010\ny\nCar Two\n200\n2015\nn\n";
        Input input = createInputWith(simulatedInput);
        
        List<Car> cars = input.execute();
        
        assertNotNull(cars);
        assertEquals(2, cars.size());
        assertEquals("Car One", cars.get(0).getModelName());
        assertEquals("Car Two", cars.get(1).getModelName());
    }

    @Test
    void testConsoleInput_InvalidJsonFallbackToManual() {
        // Пользователь вставил битый JSON, программа должна распознать ошибку и перейти к ручному вводу
        // "{invalid_json" -> ошибка парсинга -> запрос модели -> "Fallback Car" -> 90 -> 2000 -> n
        String simulatedInput = "{invalid_json\nFallback Car\n90\n2000\nn\n";
        Input input = createInputWith(simulatedInput);
        
        List<Car> cars = input.execute();
        
        assertNotNull(cars);
        assertEquals(1, cars.size());
        assertEquals("Fallback Car", cars.get(0).getModelName());
        assertEquals(90, cars.get(0).getPower());
        assertEquals(2000, cars.get(0).getProductionYear()); // Обновлено
    }

    @Test
    void testValidation_FailsOnNegativePower() {
        // Проверяем, что валидация работает и при ручном вводе
        // Ввод: модель, затем отрицательная мощность (будет отвергнута), затем правильная, затем год, затем выход
        String simulatedInput = "Test Car\n-50\n100\n2020\nn\n";
        Input input = createInputWith(simulatedInput);
        
        List<Car> cars = input.execute();
        
        // Должна проигнорировать -50 и принять 100
        assertEquals(1, cars.size());
        assertEquals(100, cars.get(0).getPower());
    }
}