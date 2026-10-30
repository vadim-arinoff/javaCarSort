package ru.team114.carsort.sorting;
import java.util.Comparator;
import ru.team114.carsort.collection.MutableSequence;

public interface SortStrategy<T> {
    void sort(MutableSequence<T> elements, Comparator<? super T> comparator);
}
