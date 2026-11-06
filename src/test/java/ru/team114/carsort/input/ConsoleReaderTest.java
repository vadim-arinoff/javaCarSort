package ru.team114.carsort.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsoleReaderTest {

    @Mock
    private Scanner scanner;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper();
    }

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
        ConsoleReader reader = new ConsoleReader(scanner, "Audi", mapper);

        when(scanner.nextLine())
                .thenReturn("0") // targetCount = 0 (бесконечный интерактивный режим)
                .thenReturn("150") // power
                .thenReturn("2015") // year
                .thenReturn("n"); // добавить еще? (прерывает цикл)

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
                .thenReturn("1") // targetCount
                .thenReturn("MyCar") // model
                .thenReturn("-10") // invalid power (отрицательная)
                .thenReturn("120") // valid power
                .thenReturn("1800") // invalid year (< 1886)
                .thenReturn("3000") // invalid year (> текущего года)
                .thenReturn("2020") // valid year
                .thenReturn("n"); // завершение ввода

        CustomCollection<Car> result = reader.read();

        assertEquals(1, result.size());

        // Проверяем, что сохранились именно валидные значения, а не invalid
        Car car = result.get(0);
        assertEquals("MyCar", car.getModelName());
        assertEquals(120, car.getPower(), "Мощность должна быть 120 после отсева -10");
        assertEquals(2020, car.getProductionYear(), "Год должен быть 2020 после отсева 1800 и 3000");
    }
}