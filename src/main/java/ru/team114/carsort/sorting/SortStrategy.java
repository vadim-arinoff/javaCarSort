package ru.team114.carsort.sorting;
import java.util.Comparator;

import ru.team114.carsort.collection.CustomCollection;


public interface SortStrategy<T> {
    void sort(CustomCollection<T> elements, Comparator<? super T> comparator);
}
