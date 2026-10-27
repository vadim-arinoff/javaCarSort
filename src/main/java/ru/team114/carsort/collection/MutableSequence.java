package ru.team114.carsort.collection;

public interface MutableSequence<T> {
    void add(T element);
    
    T get(int index);

    void set(int index, T element);

    int size();
}
