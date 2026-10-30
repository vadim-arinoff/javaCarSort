package ru.team114.carsort.sorting;

import java.util.ArrayList;
import java.util.List;
import ru.team114.carsort.collection.MutableSequence;

class TestMutableSequence<T> implements MutableSequence<T> {

    private final List<T> elements = new ArrayList<>();

    @Override
    public void add(T element) {
        elements.add(element);
    }

    @Override
    public T get(int index) {
        return elements.get(index);
    }

    @Override
    public void set(int index, T element) {
        elements.set(index, element);
    }

    @Override
    public int size() {
        return elements.size();
    }
}