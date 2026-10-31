package ru.team114.carsort.sorting;

import ru.team114.carsort.collection.CustomCollection;

import java.util.Comparator;
import java.util.function.ToIntFunction;

public class EvenOddSorter<T> implements SortStrategy<T> {

    private final ToIntFunction<T> numericFieldExtractor;

    public EvenOddSorter(ToIntFunction<T> numericFieldExtractor) {
        if (numericFieldExtractor == null) {
            throw new IllegalArgumentException("Numeric field extractor cannot be null");
        }
        this.numericFieldExtractor = numericFieldExtractor;
    }

    @Override
    public void sort(CustomCollection<T> elements, Comparator<? super T> comparator) {
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

        CustomCollection<Integer> evenIndices = new CustomCollection<>();
        CustomCollection<T> evenElements = new CustomCollection<>();

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

        //Сортировка пузырьком только чётных
        for (int a = 0; a < size - 1; a++) {
            for (int b = 0; b < size - 1 - a; b++) {
                if (comparator.compare(evenElements.get(b), evenElements.get(b + 1)) > 0) {
                    T temp = evenElements.get(b);
                    evenElements.set(b, evenElements.get(b + 1));
                    evenElements.set(b + 1, temp);
                }
            }
        }

        //Возврат отсортированных чётных элементов на их прежние места
        for (int k = 0; k < evenIndices.size(); k++) {
            elements.set(evenIndices.get(k), evenElements.get(k));
        }
    }
}