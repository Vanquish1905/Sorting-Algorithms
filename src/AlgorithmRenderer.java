import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AlgorithmRenderer {

    private static final String N2 = "O(n\u00b2)";
    private static final String NLOGN = "O(n log n)";

    // the array that gets sorted in every animation
    private static final int[] START = {7, 2, 9, 4, 1, 8, 3, 10, 5, 6};

    private Map<String, List<Frame>> cache = new HashMap<>();

    public boolean renderBubbleSort(Graphics2D g, Rectangle area, int tick) {
        if (!cache.containsKey("BubbleSort")) {
            cache.put("BubbleSort", bubbleFrames());
        }
        String[] stats = {"Best", "O(n)", "Average", N2, "Worst", N2, "Memory", "O(1)"};
        String[] legend = {null, "being compared", null, "in its final place", null, null};
        return drawFrames(g, area, cache.get("BubbleSort"), tick, "Bubble Sort",
                "Goes through the array and swaps neighbours that are in the wrong order. "
                        + "After every pass the biggest remaining value has moved to the end. "
                        + "When a pass needs no swap at all, the array is sorted.",
                stats, legend, "");
    }

    public boolean renderSelectionSort(Graphics2D g, Rectangle area, int tick) {
        if (!cache.containsKey("SelectionSort")) {
            cache.put("SelectionSort", selectionFrames());
        }
        String[] stats = {"Best", N2, "Average", N2, "Worst", N2, "Memory", "O(1)"};
        String[] legend = {null, "being checked", "smallest so far", "sorted", "slot to fill", null};
        return drawFrames(g, area, cache.get("SelectionSort"), tick, "Selection Sort",
                "Looks through the unsorted part for the smallest value and swaps it to the front. "
                        + "The sorted part grows by one slot per round.",
                stats, legend, "");
    }

    public boolean renderInsertionSort(Graphics2D g, Rectangle area, int tick) {
        if (!cache.containsKey("InsertionSort")) {
            cache.put("InsertionSort", insertionFrames());
        }
        String[] stats = {"Best", "O(n)", "Average", N2, "Worst", N2, "Memory", "O(1)"};
        String[] legend = {null, "compared with key", "key", "sorted part", null, null};
        return drawFrames(g, area, cache.get("InsertionSort"), tick, "Insertion Sort",
                "Takes the next value (the key), shifts every bigger value in the sorted part one slot "
                        + "to the right and drops the key into the gap. Works like sorting cards in your hand.",
                stats, legend, "key in hand");
    }

    public boolean renderQuickSort(Graphics2D g, Rectangle area, int tick) {
        if (!cache.containsKey("QuickSort")) {
            cache.put("QuickSort", quickFrames());
        }
        String[] stats = {"Best", NLOGN, "Average", NLOGN, "Worst", N2, "Memory", "O(log n)"};
        String[] legend = {null, "being checked", "pivot", "final place", "smaller than pivot", "bigger than pivot"};
        return drawFrames(g, area, cache.get("QuickSort"), tick, "Quick Sort",
                "Picks the last value as pivot and moves smaller values to its left and bigger ones "
                        + "to its right. The pivot is then in its final place and both sides are sorted the same way.",
                stats, legend, "");
    }

    public boolean renderMergeSort(Graphics2D g, Rectangle area, int tick) {
        if (!cache.containsKey("MergeSort")) {
            cache.put("MergeSort", mergeFrames());
        }
        String[] stats = {"Best", NLOGN, "Average", NLOGN, "Worst", NLOGN, "Memory", "O(n)"};
        String[] legend = {null, "compared", null, "merged", "left half", "right half"};
        return drawFrames(g, area, cache.get("MergeSort"), tick, "Merge Sort",
                "Splits the array in half again and again until single values are left, then merges "
                        + "the halves back together. Merging two sorted halves only needs to compare the front values.",
                stats, legend, "copies of the two halves");
    }


    private List<Frame> bubbleFrames() {
        List<Frame> frames = new ArrayList<>();
        int[] a = START.clone();
        boolean[] done = new boolean[a.length];
        int end = a.length;
        boolean swapped = true;

        while (swapped) {
            swapped = false;
            for (int i = 0; i < end - 1; i++) {
                int[] marks = marksFrom(done);
                marks[i] = 1;
                marks[i + 1] = 1;
                if (a[i] > a[i + 1]) {
                    frames.add(new Frame(a, marks, a[i] + " > " + a[i + 1] + ", swap them"));
                    swap(a, i, i + 1);
                    swapped = true;
                } else {
                    frames.add(new Frame(a, marks, a[i] + " <= " + a[i + 1] + ", leave them"));
                }
            }
            end--;
            done[end] = true;
        }

        frames.add(new Frame(a, filled(a.length, 3), "No swaps in the last pass, the array is sorted"));
        return frames;
    }

    private List<Frame> selectionFrames() {
        List<Frame> frames = new ArrayList<>();
        int[] a = START.clone();
        boolean[] done = new boolean[a.length];

        for (int i = 0; i < a.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) {
                int[] marks = marksFrom(done);
                marks[i] = 4;
                marks[min] = 2;
                marks[j] = 1;
                String text;
                if (a[j] < a[min]) {
                    text = a[j] + " is smaller than " + a[min] + ", new smallest";
                } else {
                    text = a[j] + " is not smaller than " + a[min];
                }
                frames.add(new Frame(a, marks, text));
                if (a[j] < a[min]) {
                    min = j;
                }
            }

            int[] marks = marksFrom(done);
            marks[i] = 4;
            marks[min] = 2;
            if (min == i) {
                frames.add(new Frame(a, marks, a[i] + " is already the smallest, it stays"));
            } else {
                frames.add(new Frame(a, marks, "Swap " + a[i] + " with the smallest value " + a[min]));
            }
            swap(a, i, min);
            done[i] = true;
        }

        frames.add(new Frame(a, filled(a.length, 3), "Only one value left, the array is sorted"));
        return frames;
    }

    private List<Frame> insertionFrames() {
        List<Frame> frames = new ArrayList<>();
        int[] a = START.clone();
        int n = a.length;

        int[] first = new int[n];
        first[0] = 3;
        frames.add(new Frame(a, first, "The first value counts as a sorted list of one"));

        for (int i = 1; i < n; i++) {
            int key = a[i];
            int hole = i;
            a[hole] = 0;
            int j = i - 1;

            int[] marks = prefix(n, i);
            frames.add(new Frame(a, marks, single(n, hole, key), single(n, hole, 2),
                    "Pick up " + key + " and find its place in the sorted part"));

            while (j >= 0 && a[j] > key) {
                marks = prefix(n, i);
                marks[j] = 1;
                frames.add(new Frame(a, marks, single(n, hole, key), single(n, hole, 2),
                        a[j] + " > " + key + ", shift " + a[j] + " to the right"));
                a[hole] = a[j];
                a[j] = 0;
                hole = j;
                j--;
            }

            marks = prefix(n, i);
            String text;
            if (j >= 0) {
                marks[j] = 1;
                text = a[j] + " <= " + key + ", the gap is the right spot for " + key;
            } else {
                text = "Reached the start, the gap is the right spot for " + key;
            }
            frames.add(new Frame(a, marks, single(n, hole, key), single(n, hole, 2), text));
            a[hole] = key;
        }

        frames.add(new Frame(a, filled(n, 3), "Every value has been inserted, the array is sorted"));
        return frames;
    }

    private List<Frame> quickFrames() {
        List<Frame> frames = new ArrayList<>();
        int[] a = START.clone();
        boolean[] done = new boolean[a.length];
        quickStep(a, 0, a.length - 1, done, frames);
        frames.add(new Frame(a, filled(a.length, 3), "All pivots are in place, the array is sorted"));
        return frames;
    }

    private void quickStep(int[] a, int start, int end, boolean[] done, List<Frame> frames) {
        if (end < start) {
            return;
        }
        if (end == start) {
            done[start] = true;
            frames.add(new Frame(a, marksFrom(done), "A single value (" + a[start] + ") is already in place"));
            return;
        }

        int pivot = a[end];
        int i = start - 1;

        for (int j = start; j <= end - 1; j++) {
            int[] marks = marksFrom(done);
            for (int p = start; p <= i; p++) {
                marks[p] = 4;
            }
            for (int p = i + 1; p < j; p++) {
                marks[p] = 5;
            }
            marks[j] = 1;
            marks[end] = 2;

            if (a[j] < pivot) {
                frames.add(new Frame(a, marks, a[j] + " < " + pivot + ", it goes to the small side"));
                i++;
                swap(a, i, j);
            } else {
                frames.add(new Frame(a, marks, a[j] + " >= " + pivot + ", it stays on the big side"));
            }
        }

        i++;
        int[] marks = marksFrom(done);
        for (int p = start; p < i; p++) {
            marks[p] = 4;
        }
        for (int p = i; p < end; p++) {
            marks[p] = 5;
        }
        marks[end] = 2;
        frames.add(new Frame(a, marks, "Move the pivot " + pivot + " between the small and the big side"));

        swap(a, i, end);
        done[i] = true;
        frames.add(new Frame(a, marksFrom(done), pivot + " is now in its final place"));

        quickStep(a, start, i - 1, done, frames);
        quickStep(a, i + 1, end, done, frames);
    }

    private List<Frame> mergeFrames() {
        List<Frame> frames = new ArrayList<>();
        int[] a = START.clone();
        mergeStep(a, 0, a.length - 1, frames);
        frames.add(new Frame(a, filled(a.length, 3), "Everything is merged, the array is sorted"));
        return frames;
    }

    private void mergeStep(int[] a, int start, int end, List<Frame> frames) {
        if (start >= end) {
            return;
        }
        int mid = (start + end) / 2;

        int[] splitMarks = new int[a.length];
        for (int p = start; p <= mid; p++) {
            splitMarks[p] = 4;
        }
        for (int p = mid + 1; p <= end; p++) {
            splitMarks[p] = 5;
        }
        frames.add(new Frame(a, splitMarks,
                "Split positions " + (start + 1) + " to " + (end + 1) + " into two halves"));

        mergeStep(a, start, mid, frames);
        mergeStep(a, mid + 1, end, frames);

        // the two halves are copied to a buffer, same as in MergeSort.merge
        int[] buffer = new int[a.length];
        int[] bufferMarks = new int[a.length];
        for (int p = start; p <= mid; p++) {
            buffer[p] = a[p];
            bufferMarks[p] = 4;
        }
        for (int p = mid + 1; p <= end; p++) {
            buffer[p] = a[p];
            bufferMarks[p] = 5;
        }

        int i = start;      // position in the buffer, left half
        int j = mid + 1;    // position in the buffer, right half
        int k = start;      // position in the real array

        while (i <= mid && j <= end) {
            int[] bm = bufferMarks.clone();
            bm[i] = 1;
            bm[j] = 1;
            String text;
            if (buffer[i] <= buffer[j]) {
                text = buffer[i] + " <= " + buffer[j] + ", take " + buffer[i] + " from the left";
            } else {
                text = buffer[i] + " > " + buffer[j] + ", take " + buffer[j] + " from the right";
            }
            frames.add(new Frame(blanked(a, k, end), written(a.length, start, k), buffer, bm, text));

            if (buffer[i] <= buffer[j]) {
                a[k] = buffer[i];
                buffer[i] = 0;
                i++;
            } else {
                a[k] = buffer[j];
                buffer[j] = 0;
                j++;
            }
            k++;
        }

        while (i <= mid) {
            int[] bm = bufferMarks.clone();
            bm[i] = 1;
            frames.add(new Frame(blanked(a, k, end), written(a.length, start, k), buffer, bm,
                    "The right half is used up, copy " + buffer[i]));
            a[k] = buffer[i];
            buffer[i] = 0;
            i++;
            k++;
        }

        while (j <= end) {
            int[] bm = bufferMarks.clone();
            bm[j] = 1;
            frames.add(new Frame(blanked(a, k, end), written(a.length, start, k), buffer, bm,
                    "The left half is used up, copy " + buffer[j]));
            a[k] = buffer[j];
            buffer[j] = 0;
            j++;
            k++;
        }

        int[] merged = new int[a.length];
        for (int p = start; p <= end; p++) {
            merged[p] = 3;
        }
        frames.add(new Frame(a, merged, "Positions " + (start + 1) + " to " + (end + 1) + " are merged"));
    }


    private void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // marks array where every finished position has code 3
    private int[] marksFrom(boolean[] done) {
        int[] marks = new int[done.length];
        for (int i = 0; i < done.length; i++) {
            if (done[i]) {
                marks[i] = 3;
            }
        }
        return marks;
    }

    private int[] filled(int n, int code) {
        int[] marks = new int[n];
        for (int i = 0; i < n; i++) {
            marks[i] = code;
        }
        return marks;
    }

    private int[] single(int n, int index, int value) {
        int[] result = new int[n];
        result[index] = value;
        return result;
    }

    // positions 0 to last are the sorted part
    private int[] prefix(int n, int last) {
        int[] marks = new int[n];
        for (int i = 0; i <= last; i++) {
            marks[i] = 3;
        }
        return marks;
    }

    // copy of the array where from..to is shown as empty
    private int[] blanked(int[] a, int from, int to) {
        int[] copy = a.clone();
        for (int i = from; i <= to; i++) {
            copy[i] = 0;
        }
        return copy;
    }

    // positions start to k - 1 have already been written by the merge
    private int[] written(int n, int start, int k) {
        int[] marks = new int[n];
        for (int i = start; i < k; i++) {
            marks[i] = 3;
        }
        return marks;
    }

    private int maxValue() {
        int max = 0;
        for (int v : START) {
            max = Math.max(max, v);
        }
        return max;
    }

    private boolean drawFrames(Graphics2D g, Rectangle area, List<Frame> frames, int tick,
                               String title, String blurb, String[] stats, String[] legend, String auxName) {
        int last = frames.size() - 1;
        int index = Math.min(tick, last);
        Frame f = frames.get(index);
        int bottom = area.y + area.height;

        g.setFont(Theme.TITLE);
        g.setColor(Theme.TEXT);
        g.drawString(title, area.x, area.y + 22);
        drawStats(g, area, stats);

        g.setFont(Theme.NORMAL);
        g.setColor(Theme.TEXT_DIM);
        int textEnd = drawWrapped(g, blurb, area.x, area.y + 74, Math.min(area.width, 700));

        int barsTop = Math.max(textEnd + 30, area.y + 140);
        int barsBottom = Math.max(bottom - 100, barsTop + 80);
        int max = maxValue();

        if (f.aux == null) {
            drawRow(g, f.values, f.marks, area.x, barsTop, area.width, barsBottom - barsTop, max, true);
        } else {
            int gap = 30;
            int rowHeight = (barsBottom - barsTop - gap) / 2;
            drawRow(g, f.values, f.marks, area.x, barsTop, area.width, rowHeight, max, true);
            g.setFont(Theme.SMALL);
            g.setColor(Theme.TEXT_DIM);
            g.drawString(auxName, area.x, barsTop + rowHeight + 20);
            drawRow(g, f.aux, f.auxMarks, area.x, barsTop + rowHeight + gap, area.width, rowHeight, max, false);
        }

        // caption for the current step
        g.setFont(Theme.CAPTION);
        g.setColor(Theme.TEXT);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(f.text, area.x + (area.width - fm.stringWidth(f.text)) / 2, bottom - 74);

        // step counter and progress line
        g.setFont(Theme.SMALL);
        g.setColor(Theme.TEXT_DIM);
        fm = g.getFontMetrics();
        String stepText = "step " + (index + 1) + " / " + frames.size();
        g.drawString(stepText, area.x + area.width - fm.stringWidth(stepText), bottom - 50);
        g.setColor(Theme.BORDER);
        g.fillRect(area.x, bottom - 40, area.width, 3);
        g.setColor(Theme.ACCENT);
        g.fillRect(area.x, bottom - 40, (int) ((double) area.width * index / Math.max(1, last)), 3);

        // legend
        int lx = area.x;
        for (int code = 0; code < legend.length; code++) {
            if (legend[code] == null) {
                continue;
            }
            g.setColor(Theme.MARK_COLORS[code]);
            g.fillRoundRect(lx, bottom - 19, 12, 12, 4, 4);
            g.setColor(Theme.TEXT_DIM);
            g.drawString(legend[code], lx + 18, bottom - 9);
            lx += 18 + fm.stringWidth(legend[code]) + 24;
        }

        return tick >= last;
    }

    private void drawStats(Graphics2D g, Rectangle area, String[] stats) {
        int chipW = 96;
        int chipH = 46;
        int gap = 8;
        int count = stats.length / 2;
        int x = area.x + area.width - count * (chipW + gap) + gap;

        for (int i = 0; i < count; i++) {
            g.setColor(Theme.PANEL_LIGHT);
            g.fillRoundRect(x, area.y, chipW, chipH, 8, 8);
            g.setFont(Theme.SMALL);
            g.setColor(Theme.TEXT_DIM);
            g.drawString(stats[i * 2], x + 10, area.y + 18);
            g.setFont(Theme.BOLD);
            g.setColor(Theme.TEXT);
            g.drawString(stats[i * 2 + 1], x + 10, area.y + 37);
            x += chipW + gap;
        }
    }

    // draws text with line breaks, returns the y of the last line
    private int drawWrapped(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String line = "";
        for (String word : text.split(" ")) {
            String test = line.isEmpty() ? word : line + " " + word;
            if (fm.stringWidth(test) > maxWidth && !line.isEmpty()) {
                g.drawString(line, x, y);
                y += fm.getHeight();
                line = word;
            } else {
                line = test;
            }
        }
        g.drawString(line, x, y);
        return y;
    }

    // emptySlots: draw a dashed outline where a value is missing (only wanted in the main row)
    private void drawRow(Graphics2D g, int[] values, int[] marks, int x, int top, int width, int height,
                         int max, boolean emptySlots) {
        int n = values.length;
        double slot = (double) width / n;
        int barW = (int) (slot * 0.72);
        int labelSpace = 24;
        int barMaxH = height - labelSpace;

        g.setFont(Theme.NORMAL);
        FontMetrics fm = g.getFontMetrics();

        for (int i = 0; i < n; i++) {
            int bx = (int) (x + i * slot + (slot - barW) / 2);

            if (values[i] == 0) {
                if (!emptySlots) {
                    continue;
                }
                int h = barMaxH / 3;
                g.setColor(Theme.BORDER);
                g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, new float[] {4f, 4f}, 0f));
                g.drawRoundRect(bx, top + height - h, barW, h, 8, 8);
                g.setStroke(new BasicStroke(1f));
                continue;
            }

            int h = (int) ((double) values[i] / max * barMaxH);
            int by = top + height - h;
            g.setColor(Theme.MARK_COLORS[marks[i]]);
            g.fillRoundRect(bx, by, barW, h, 8, 8);

            String label = String.valueOf(values[i]);
            g.setColor(Theme.TEXT);
            g.drawString(label, bx + (barW - fm.stringWidth(label)) / 2, by - 6);
        }

        g.setColor(Theme.BORDER);
        g.drawLine(x, top + height, x + width, top + height);
    }

    private static class Frame {
        int[] values;     // the array, 0 means an empty slot
        int[] marks;      // color code for every position
        int[] aux;        // optional second row, can be null
        int[] auxMarks;
        String text;

        Frame(int[] values, int[] marks, String text) {
            this(values, marks, null, null, text);
        }

        Frame(int[] values, int[] marks, int[] aux, int[] auxMarks, String text) {
            this.values = values.clone();
            this.marks = marks.clone();
            this.aux = (aux == null) ? null : aux.clone();
            this.auxMarks = (auxMarks == null) ? null : auxMarks.clone();
            this.text = text;
        }
    }
}
