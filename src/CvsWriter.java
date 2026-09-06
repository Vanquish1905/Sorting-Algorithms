import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CvsWriter {

    /**
     * Writes coordinate pairs from a 2D array into a CSV file inside [project_root]/results/.
     *
     * @param fileName Name of the output file (e.g., "points" or "points.csv").
     * @param points   2D array where each row contains at least two numbers [x, y].
     */
    public static void writePointsToCsv(String fileName, double[][] points) {
        // Ensure the file ends with .csv
        if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName += ".csv";
        }

        // Target directory: [project root]/results/
        File resultsDir = new File("results");

        // Create the directory if it does not exist
        if (!resultsDir.exists()) {
            boolean created = resultsDir.mkdirs();
            if (created) {
                System.out.println("Directory created: " + resultsDir.getAbsolutePath());
            }
        }

        File outputFile = new File(resultsDir, fileName);

        // Build CSV content
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("X;Y\n"); // Header line

        // Iterate through the array and write pairs of two numbers [x, y]
        for (double[] point : points) {
            if (point != null && point.length >= 2) {
                double x = point[0];
                double y = point[1];
                csvBuilder.append(String.format("%.2f;%.2f\n", x, y));
            }
        }

        // Write content to disk
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(csvBuilder.toString());
            System.out.println("CSV successfully saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Example Usage
    public static void main(String[] args) {
        // Sample 2D array of coordinate pairs [x, y]
        double[][] coordinates = {
                {50.0, 50.0},
                {150.0, 80.0},
                {200.0, 220.0},
                {80.0, 180.0},
                {300.0, 120.0}
        };

        // Call method: creates 'results/my_plot.csv'
        writePointsToCsv("my_plot", coordinates);
    }
}