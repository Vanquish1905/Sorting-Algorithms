import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SimpleRunTimeAnalysis {

    // Runs sorting on an array and measures elapsed time in milliseconds
    public static long Analysis(int[] array) {
        Sorter sorter = new SelectionSort();
        long startTime = System.currentTimeMillis();
        sorter.sort(array);
        long end = System.currentTimeMillis();
        return end - startTime;
    }

    // Generates a random array of the specified length
    public static int[] createArray(int length) {
        int[] array = new int[length];
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(length);
        }
        return array;
    }

    // Calculates average execution time for a given array length across multiple cycles
    public static long average(int cycles, int length) {
        long result = 0;
        for (int i = 0; i < cycles; i++) {
            result += Analysis(createArray(length));
        }
        long avg = result / cycles;
        System.out.println("Length: " + length + " -> " + avg + " ms");
        return avg;
    }

    // Strategy 1: Uses a custom array of explicit lengths
    public static void runCustomLengths(int cycles, int[] lengths, String fileName) {
        double[][] points = new double[lengths.length][2];
        for (int i = 0; i < lengths.length; i++) {
            int len = lengths[i];
            long avgTime = average(cycles, len);
            points[i][0] = len;      // X coordinate: Array length
            points[i][1] = avgTime;  // Y coordinate: Execution time (ms)
        }
        writePlotToCsv(fileName, points);
    }

    // Strategy 2: Starts at startLength and increments by step for a set number of steps
    public static void runIncrementalLengths(int cycles, int startLength, int step, int totalSteps, String fileName) {
        double[][] points = new double[totalSteps][2];
        for (int i = 0; i < totalSteps; i++) {
            int currentLength = startLength + (i * step);
            long avgTime = average(cycles, currentLength);
            points[i][0] = currentLength; // X coordinate
            points[i][1] = avgTime;       // Y coordinate
        }
        writePlotToCsv(fileName, points);
    }

    /**
     * Writes pairs of numbers [x, y] into a CSV file under /results/
     */
    public static void writePlotToCsv(String fileName, double[][] dataPoints) {
        // Ensure the file ends with .csv
        if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName += ".csv";
        }

        // Target path: [project root]/results/
        File resultsDir = new File("results");

        // Create directory if it does not exist
        if (!resultsDir.exists()) {
            boolean created = resultsDir.mkdirs();
            if (created) {
                System.out.println("Directory created: " + resultsDir.getAbsolutePath());
            }
        }

        File outputFile = new File(resultsDir, fileName);

        StringBuilder csvBuilder = new StringBuilder();
        // Optional CSV Header
        csvBuilder.append("Length;TimeMs\n");

        // Iterate through 2D array and write only pairs of two numbers (X, Y)
        for (double[] point : dataPoints) {
            if (point != null && point.length >= 2) {
                csvBuilder.append(String.format("%.0f;%.2f\n", point[0], point[1]));
            }
        }

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(csvBuilder.toString());
            System.out.println("CSV successfully saved to: " + outputFile.getAbsolutePath() + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int cycles = 3;

        /*
        int[] customLengths = {5000, 10000, 20000, 40000, 60000};
        System.out.println("Running Custom Length Analysis...");
        runCustomLengths(cycles, customLengths, "custom_lengths_runtime");

         */

        int startLength = 5000;
        int step = 5000;
        int totalSteps = 10;
        System.out.println("Running Incremental Length Analysis...");
        runIncrementalLengths(cycles, startLength, step, totalSteps, "SelectionSort");
    }
}