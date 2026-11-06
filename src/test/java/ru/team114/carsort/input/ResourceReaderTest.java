package ru.team114.carsort.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.model.Car;

import static org.junit.jupiter.api.Assertions.*;

class ResourceReaderTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

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
        ResourceReader reader = new ResourceReader("test_car.json", mapper);
        CustomCollection<Car> result = reader.read();

        assertNotNull(result, "Коллекция не должна быть null");

        assertEquals(8, result.size(), "Должны быть прочитаны все машины из test_car.json (8)");

        assertEquals("Toyota Camry", result.get(0).getModelName());
        assertEquals("Audi R8", result.get(1).getModelName());
        assertEquals("Corvette C6", result.get(2).getModelName());
        assertEquals("Honda Accord Gen 6", result.get(3).getModelName());
        assertEquals("Toyota Mark II", result.get(4).getModelName());
    }
}