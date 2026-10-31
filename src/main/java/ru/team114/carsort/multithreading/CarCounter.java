package ru.team114.carsort.multithreading;

import ru.team114.carsort.collection.CustomCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CarCounter {
    public static <T> void countOccurrences(CustomCollection<T> elements, T target) {
        if (elements == null) {
            throw new IllegalArgumentException("Collection cannot be null");
        }

        int size = elements.size();
        if (size == 0) {
            System.out.println("Количество вхождений элемента: 0");
            return;
        }

        // Количество потоков не больше количества ядер и не больше размера коллекции
        int threadCount = Math.min(Runtime.getRuntime().availableProcessors(), size);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            List<Future<Integer>> futures = new ArrayList<>(threadCount);

            // Размер чанка и остаток для равномерного распределения
            int chunkSize = size / threadCount;
            int remainder = size % threadCount;

            int start = 0;
            for (int i = 0; i < threadCount; i++) {
                // Распределяем остаток по первым потокам
                int end = start + chunkSize + (i < remainder ? 1 : 0);

                final int finalStart = start;
                final int finalEnd = end;

                Callable<Integer> task = () -> {
                    int count = 0;
                    for (int j = finalStart; j < finalEnd; j++) {
                        T item = elements.get(j);
                        // Безопасное сравнение учитывая null
                        if (item == null ? target == null : item.equals(target)) {
                            count++;
                        }
                    }
                    return count;
                };

                futures.add(executor.submit(task));
                start = end; // Следующий поток начнёт подсчёт с индекса end
            }

            // Собираем результаты — future.get() блокирует, пока все потоки не завершатся
            int totalCount = 0;
            for (Future<Integer> future : futures) {
                try {
                    totalCount += future.get();
                } catch (InterruptedException e) {
                    System.err.println("Ошибка при выполнении потока: " + e.getMessage());
                    Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания
                } catch (ExecutionException e) {
                    System.err.println("Ошибка внутри потока: " + e.getCause());
                }
            }

            System.out.println("Количество вхождений элемента: " + totalCount);

        } finally {
            // Гарантированно закрываем пул потоков, даже если что-то упало
            executor.shutdown();
        }
    }
}