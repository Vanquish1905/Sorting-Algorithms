import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class SimpleRunTimeAnalysis {

    // Runs the given sorter on an array and measures the time in milliseconds
    public static long Analysis(Sorter sorter, int[] array) {
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

    // Average time for a given array length across multiple cycles
    public static long average(Sorter sorter, int cycles, int length) {
        long result = 0;
        for (int i = 0; i < cycles; i++) {
            result += Analysis(sorter, createArray(length));
        }
        long avg = result / cycles;
        System.out.println("Length: " + length + " -> " + avg + " ms");
        return avg;
    }

    // Strategy 1: custom array of explicit lengths
    public static void runCustomLengths(Sorter sorter, int cycles, int[] lengths, String fileName) {
        double[][] points = new double[lengths.length][2];
        for (int i = 0; i < lengths.length; i++) {
            int len = lengths[i];
            long avgTime = average(sorter, cycles, len);
            points[i][0] = len;      // X: array length
            points[i][1] = avgTime;  // Y: time (ms)
        }
        writePlotToCsv(fileName, points);
    }

    // Strategy 2: start at startLength and add step for a set number of steps
    public static void runIncrementalLengths(Sorter sorter, int cycles, int startLength, int step, int totalSteps, String fileName) {
        double[][] points = new double[totalSteps][2];
        for (int i = 0; i < totalSteps; i++) {
            int currentLength = startLength + (i * step);
            long avgTime = average(sorter, cycles, currentLength);
            points[i][0] = currentLength;
            points[i][1] = avgTime;
        }
        writePlotToCsv(fileName, points);
    }

    /**
     * Writes pairs of numbers [x, y] into a CSV file under /results/
     */
    public static void writePlotToCsv(String fileName, double[][] dataPoints) {
        // Ensure the file extension is removed before appending timestamp
        if (fileName.toLowerCase().endsWith(".csv")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }

        // Format current German time (Europe/Berlin) as YYYY-MM-DD_HH-mm-ss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = ZonedDateTime.now(ZoneId.of("Europe/Berlin")).format(formatter);

        // Append the clean timestamp and file extension
        fileName += "_" + timestamp + ".csv";

        File resultsDir = new File("results");

        if (!resultsDir.exists()) {
            boolean created = resultsDir.mkdirs();
            if (created) {
                System.out.println("Directory created: " + resultsDir.getAbsolutePath());
            }
        }

        File outputFile = new File(resultsDir, fileName);

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Length;TimeMs\n");

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
        int cycles = 1;
        int startLength = 1;
        int step = 1;
        int totalSteps = 1;

        //System.out.println("Running SelectionSort...");
        //runIncrementalLengths(new SelectionSort(), cycles, startLength, step, totalSteps, "SelectionSort");

        //System.out.println("Running BubbleSort...");
        //runIncrementalLengths(new BubbleSort(), cycles, startLength, step, totalSteps, "BubbleSort");

        //System.out.println("Running InsertionSort...");
        //runIncrementalLengths(new InsertionSort(), cycles, startLength, step, totalSteps, "InsertionSort");

        System.out.println("Running QuickSort...");
        runIncrementalLengths(new QuickSort(), cycles, startLength, step, totalSteps, "QuickSort");
    }
}