package ru.team114.carsort.sorting;

import java.util.Comparator;
import ru.team114.carsort.collection.MutableSequence;

public class SelectionSortStrategy<T> implements SortStrategy<T> {

    @Override
    public void sort(
            MutableSequence<T> elements,
            Comparator<? super T> comparator
    ) {
        for (int i = 0; i < elements.size() - 1; i++) {

            int minIndex = i;

            for (int j = i + 1; j < elements.size(); j++) {
                if (comparator.compare(
                        elements.get(j),
                        elements.get(minIndex)
                ) < 0) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                T temp = elements.get(i);

                elements.set(i, elements.get(minIndex));
                elements.set(minIndex, temp);
            }
        }
    }
}