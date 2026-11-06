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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    @DisplayName("Input: Маршрутизация на ResourceReader (относительный путь)")
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