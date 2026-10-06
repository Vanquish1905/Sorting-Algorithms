import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultsLoader {

    // matches the timestamp SimpleRunTimeAnalysis puts at the end of the file name
    private static final Pattern TIMESTAMP =
            Pattern.compile("_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}$");

    public static List<Series> loadAll(File folder) {
        List<Series> result = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) {
            return result;
        }
        Arrays.sort(files);
        for (File file : files) {
            if (!file.isFile() || !file.getName().toLowerCase().endsWith(".csv")) {
                continue;
            }
            Series s = loadFile(file);
            if (s != null) {
                result.add(s);
            }
        }
        return result;
    }

    public static Series loadFile(File file) {
        List<double[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                // the writer uses ; but a normal csv with , works too
                String[] parts = line.contains(";") ? line.split(";") : line.split(",");
                if (parts.length < 2) {
                    continue;
                }
                try {
                    // String.format uses the system locale, so numbers can come with a comma
                    double x = Double.parseDouble(parts[0].trim().replace(',', '.'));
                    double y = Double.parseDouble(parts[1].trim().replace(',', '.'));
                    rows.add(new double[] {x, y});
                } catch (NumberFormatException e) {
                    // this is the header line, skip it
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + file.getName() + ": " + e.getMessage());
            return null;
        }

        if (rows.isEmpty()) {
            return null;
        }
        rows.sort((a, b) -> Double.compare(a[0], b[0]));

        Series s = new Series();
        s.file = file;
        s.modified = file.lastModified();
        s.xs = new double[rows.size()];
        s.ys = new double[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            s.xs[i] = rows.get(i)[0];
            s.ys[i] = rows.get(i)[1];
        }

        String name = file.getName();
        s.label = name.substring(0, name.length() - 4);

        Matcher m = TIMESTAMP.matcher(s.label);
        if (m.find()) {
            s.algorithm = s.label.substring(0, m.start());
            String[] stamp = m.group().substring(1).split("_");
            s.runName = stamp[0] + "  " + stamp[1].replace('-', ':');
        } else {
            s.algorithm = s.label;
            s.runName = "single run";
        }
        return s;
    }
}
