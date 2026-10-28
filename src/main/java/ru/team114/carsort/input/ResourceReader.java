package ru.team114.carsort.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.team114.carsort.model.Car;
import java.io.InputStream;
import java.util.List;

public class ResourceReader implements Reader {

    private final String resourceName;
    private final ObjectMapper mapper;

    public ResourceReader(String resourceName, ObjectMapper mapper) {
        this.resourceName = resourceName;
        this.mapper = mapper;
    }

    @Override
    public List<Car> read() {
        String path = resourceName.startsWith("/") ? resourceName : "/" + resourceName;
        try (InputStream is = ResourceReader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Resource not found in classpath: " + path);
            }
            return mapper.readValue(is, new TypeReference<List<Car>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Error reading resource: " + e.getMessage(), e);
        }
    }
}