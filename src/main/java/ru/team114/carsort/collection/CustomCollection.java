package ru.team114.carsort.collection;

import java.util.Arrays;

public class CustomCollection<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size;

    public CustomCollection() {
        this(DEFAULT_CAPACITY);
    }

    public CustomCollection(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(
                    "Initial capacity cannot be negative."
            );
        }

        elements = new Object[initialCapacity];
        size = 0;
    }

    // Добавление элемента
    public void add(T element) {
        ensureCapacity(size + 1);
        elements[size] = element;
        size++;
    }

    // Чтение элемента по индексу
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);

        return (T) elements[index];
    }

    // Изменение элемента по индексу
    @SuppressWarnings("unchecked")
    public T set(int index, T element) {
        checkIndex(index);

        T oldElement = (T) elements[index];
        elements[index] = element;

        return oldElement;
    }

    // Получение количества элементов
    public int size() {
        return size;
    }

    // Проверка на пустоту
    public boolean isEmpty() {
        return size == 0;
    }

    // Увеличение вместимости массива
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= elements.length) {
            return;
        }

        int newCapacity;

        if (elements.length == 0) {
            newCapacity = 1;
        } else {
            newCapacity = elements.length * 2;
        }

        while (newCapacity < minCapacity) {
            newCapacity *= 2;
        }

        elements = Arrays.copyOf(elements, newCapacity);
    }

    // Проверка корректности индекса
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + size
            );
        }
    }
}