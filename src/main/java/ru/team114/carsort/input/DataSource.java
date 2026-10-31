package ru.team114.carsort.input;
import java.io.IOException;
import ru.team114.carsort.collection.MutableSequence;

public interface DataSource<T> {
    MutableSequence<T> read(int length) throws IOException;
}
