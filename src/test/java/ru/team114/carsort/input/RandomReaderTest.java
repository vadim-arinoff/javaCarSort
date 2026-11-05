package ru.team114.carsort.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RandomReaderTest {

    @Mock
    private Scanner scanner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
                .thenReturn("abc") // Не число
                .thenReturn("-5") // Отрицательное
                .thenReturn("0") // Ноль
                .thenReturn("2"); // Валидное

        RandomReader reader = new RandomReader(scanner);
        CustomCollection<Car> result = reader.read();

        assertEquals(2, result.size());
        verify(scanner, times(4)).nextLine();
    }
}