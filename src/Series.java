import java.awt.Color;
import java.io.File;

// one CSV file = one line in the graph
public class Series {
    public File file;
    public String label;      // file name without .csv
    public String algorithm;  // label without the timestamp, e.g. MergeSort
    public String runName;    // what is shown in the run list
    public double[] xs;       // array lengths
    public double[] ys;       // times in ms
    public Color color = Color.WHITE;
    public boolean visible = true;
    public long modified;

    public double lastY() {
        return ys[ys.length - 1];
    }
}
