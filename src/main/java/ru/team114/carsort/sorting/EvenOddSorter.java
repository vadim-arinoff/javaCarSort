package ru.team114.carsort.sorting;

import ru.team114.carsort.collection.CustomCollection;
import ru.team114.carsort.collection.MutableSequence;

import java.util.Comparator;
import java.util.function.ToIntFunction;

public class EvenOddSorter<T> implements SortStrategy<T> {
    private final ToIntFunction<T> numericFieldExtractor;
    private final SortStrategy<T> innerSortStrategy;


    public EvenOddSorter(ToIntFunction<T> numericFieldExtractor, SortStrategy<T> innerSortStrategy) {
        if (numericFieldExtractor == null) {
            throw new IllegalArgumentException("Numeric field extractor cannot be null");
        }
        if (innerSortStrategy == null) {
            throw new IllegalArgumentException("Inner sorting strategy cannot be null");
        }
        this.numericFieldExtractor = numericFieldExtractor;
        this.innerSortStrategy = innerSortStrategy;
    }

    @Override
    public void sort(MutableSequence<T> elements, Comparator<? super T> comparator) {
        if (elements == null) {
            throw new IllegalArgumentException("Elements collection cannot be null");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }

        int n = elements.size();
        if (n <= 1) {
            return;
        }

        MutableSequence<Integer> evenIndices = new CustomCollection<>();
        MutableSequence<T> evenElements = new CustomCollection<>();

        // Создание коллекции с чётными элементами и их индексами
        for (int i = 0; i < n; i++) {
            T element = elements.get(i);
            int value = numericFieldExtractor.applyAsInt(element);
            if (value % 2 == 0) {
                evenIndices.add(i);
                evenElements.add(element);
            }
        }

        // Проверка что четных элементов >1
        int size = evenElements.size();
        if (size <= 1) {
            return;
        }

        innerSortStrategy.sort(evenElements, comparator);

        // Возврат отсортированных чётных элементов на их прежние места
        for (int k = 0; k < evenIndices.size(); k++) {
            elements.set(evenIndices.get(k), evenElements.get(k));
        }
    }
}