package ru.team114.carsort.sorting;

import java.util.Comparator;
import ru.team114.carsort.collection.MutableSequence;

public class BubbleSortStrategy<T> implements SortStrategy<T> {

    @Override
    public void sort(
            MutableSequence<T> elements,
            Comparator<? super T> comparator
    ) {
        for (int i = 0; i < elements.size() - 1; i++) {
            for (int j = 0; j < elements.size() - 1 - i; j++) {

                if (comparator.compare(
                        elements.get(j),
                        elements.get(j + 1)
                ) > 0) {

                    T temp = elements.get(j);

                    elements.set(j, elements.get(j + 1));
                    elements.set(j + 1, temp);
                }
            }
        }
    }
}