package org.example;

import com.sun.source.doctree.ReturnTree;

import java.util.Arrays;
import java.util.Random;


public class Main {
    static final int[] size = {10, 100, 1_000, 2_000, 4_000, 8_000, 16_000, 32_000, 64_000, 128_000};

    static final int repeat = 5;

    static long measure(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return System.nanoTime() - start;
    }

    static double measureAverage(Runnable task, int repeat) {
        for (int i=0; i<3; i ++) task.run();
        long[] time = new long[repeat];
        for (int i = 0; i<repeat; i++) time[i] = measure(task);
        Arrays.sort(time);
        return time[repeat / 2];
    }

    static int[] generateArray(int n, Random rnd) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rnd.nextInt(n * 10);
        return a;
    }

    static int[] generateSortArray(int n, Random rnd) {
        int[] a = generateArray(n, rnd);
        Arrays.sort(a);
        return a;
    }

    static void printTable(String title, String[] cases, int[] sizes, double[][] results) {
        System.out.println("\n=== " + title + " ===");
        System.out.printf("%-12s", "N");
        for (String c : cases) System.out.printf("%-18s", c);
        System.out.println();
        for (int i = 0; i < sizes.length; i++) {
            System.out.printf("%-12d", sizes[i]);
            for (double[] result : results) System.out.printf("%-18.2f", result[i]);
            System.out.println();
        }
    }

    static void task1ArrayAccess(Random rnd) {
        String[] cases = {"array[index]"};
        double[][] results = new double[1][size.length];
        for (int s = 0; s < size.length; s++) {
            int n = size[s];
            int[] arr = generateArray(n, rnd);
            int[] indices = new int[n];
            for (int i = 0; i < n; i++) indices[i] = rnd.nextInt(n);
            final int[] idx = indices;
            final int[] a = arr;
            results[0][s] = measureAverage(() -> {
                long sum = 0;
                for (int i : idx) sum += a[i];
                if (sum == Long.MIN_VALUE) System.out.print("");
            }, repeat);
        }
        printTable("Task 1: Array access O(1)", cases, size, results);
    }

    static int binarySearch(int[] arr, int target) {
        int lo = 0, hi = arr.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            if (arr[mid] == target) return mid;
            if (arr[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    static void task2BinarySearch(Random rnd) {
        String[] cases = {"first", "random", "absent"};
        double[][] results = new double[3][size.length];
        for (int s = 0; s < size.length; s++) {
            int n = size[s];
            int[] arr = generateSortArray(n, rnd);
            int firstTarget = arr[0];
            int randomTarget = arr[rnd.nextInt(n)];
            int absentTarget = arr[n - 1] + 1;
            results[0][s] = measureAverage(() -> binarySearch(arr, firstTarget), repeat);
            results[1][s] = measureAverage(() -> binarySearch(arr, randomTarget), repeat);
            results[2][s] = measureAverage(() -> binarySearch(arr, absentTarget), repeat);
        }
        printTable("Task 2: Binary search O(log N)", cases, size, results);
    }

    static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == target) return i;
        return -1;
    }

    static void task3LinearSearch(Random rnd) {
        String[] cases = {"first", "random", "absent"};
        double[][] results = new double[3][size.length];
        for (int s = 0; s < size.length; s++) {
            int n = size[s];
            int[] arr = generateArray(n, rnd);
            int firstTarget = arr[0];
            int randomTarget = arr[rnd.nextInt(n)];
            int absentTarget = -1;
            results[0][s] = measureAverage(() -> linearSearch(arr, firstTarget), repeat);
            results[1][s] = measureAverage(() -> linearSearch(arr, randomTarget), repeat);
            results[2][s] = measureAverage(() -> linearSearch(arr, absentTarget), repeat);
        }
        printTable("Task 3: Linear search O(N)", cases, size, results);
    }

    static int countGreater(int[] arr, int target) {
        int count = 0;
        for (int v : arr) if (v > target) count++;
        return count;
    }

    static void task4CountGreater(Random rnd) {
        String[] cases = {"all match", "random", "none match"};
        double[][] results = new double[3][size.length];
        for (int s = 0; s < size.length; s++) {
            int n = size[s];
            int[] arr = generateArray(n, rnd);
            int allMatchTarget = Integer.MIN_VALUE;
            int noneMatchTarget = Integer.MAX_VALUE;
            int randomTarget = arr[rnd.nextInt(n)];
            results[0][s] = measureAverage(() -> countGreater(arr, allMatchTarget), repeat);
            results[1][s] = measureAverage(() -> countGreater(arr, randomTarget), repeat);
            results[2][s] = measureAverage(() -> countGreater(arr, noneMatchTarget), repeat);
        }
        printTable("Task 4: Count elements O(N)", cases, size, results);
    }

    static int findMax(int[] arr) {
        int max = arr[0];
        for (int v : arr) if (v > max) max = v;
        return max;
    }

    static void task5FindMax(Random rnd) {
        String[] cases = {"best", "average", "worst"};
        double[][] results = new double[3][size.length];
        for (int s = 0; s < size.length; s++) {
            int n = size[s];
            int[] best = new int[n];
            for (int i = 0; i < n; i++) best[i] = n - i;
            int[] average = generateArray(n, rnd);
            int[] worst = new int[n];
            for (int i = 0; i < n; i++) worst[i] = i + 1;
            results[0][s] = measureAverage(() -> findMax(best), repeat);
            results[1][s] = measureAverage(() -> findMax(average), repeat);
            results[2][s] = measureAverage(() -> findMax(worst), repeat);
        }
        printTable("Task 5: Find max O(N)", cases, size, results);
    }

    public static void main(String[] args) {
        Random rnd = new Random(42);
        task1ArrayAccess(rnd);
        task2BinarySearch(rnd);
        task3LinearSearch(rnd);
        task4CountGreater(rnd);
        task5FindMax(rnd);
    }

}