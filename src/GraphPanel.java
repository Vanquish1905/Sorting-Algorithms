import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GraphPanel extends JPanel {

    private List<Series> series = new ArrayList<>();
    private boolean logScale = false;
    private boolean showPoints = true;

    // space around the plot area
    private int left = 70;
    private int right = 30;
    private int top = 30;
    private int bottom = 55;

    private double xMax = 1;
    private double yMax = 1;

    private Series hoverSeries = null;
    private int hoverIndex = -1;

    public GraphPanel() {
        setBackground(Theme.PANEL);

        MouseAdapter mouse = new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                findHover(e.getX(), e.getY());
            }

            public void mouseExited(MouseEvent e) {
                if (hoverSeries != null) {
                    hoverSeries = null;
                    hoverIndex = -1;
                    repaint();
                }
            }
        };
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
    }

    public void setSeries(List<Series> list) {
        series = new ArrayList<>(list);
        hoverSeries = null;
        hoverIndex = -1;

        double maxX = 0;
        double maxY = 0;
        for (Series s : series) {
            for (int i = 0; i < s.xs.length; i++) {
                maxX = Math.max(maxX, s.xs[i]);
                maxY = Math.max(maxY, s.ys[i]);
            }
        }
        xMax = niceMax(maxX);
        yMax = niceMax(maxY);
        repaint();
    }

    public void setLogScale(boolean value) {
        logScale = value;
        repaint();
    }

    public void setShowPoints(boolean value) {
        showPoints = value;
        repaint();
    }

    // rounds a value up to 1, 2 or 5 times a power of ten so the axis numbers look clean
    private double niceMax(double v) {
        if (v <= 0) {
            return 1;
        }
        double base = Math.pow(10, Math.floor(Math.log10(v)));
        double f = v / base;
        double nice;
        if (f <= 1) {
            nice = 1;
        } else if (f <= 2) {
            nice = 2;
        } else if (f <= 5) {
            nice = 5;
        } else {
            nice = 10;
        }
        return nice * base;
    }

    private double scaleY(double y) {
        return logScale ? Math.log10(y + 1) : y;
    }

    private double toX(double x, int w) {
        return left + x / xMax * w;
    }

    private double toY(double y, int h) {
        return top + h - scaleY(y) / scaleY(yMax) * h;
    }

    private String format(double v) {
        if (v == Math.rint(v)) {
            return Long.toString((long) v);
        }
        return String.format(Locale.US, "%.1f", v);
    }

    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth() - left - right;
        int h = getHeight() - top - bottom;
        if (w < 50 || h < 50) {
            return;
        }

        if (series.isEmpty()) {
            drawEmptyMessage(g);
            return;
        }

        drawGrid(g, w, h);
        drawLines(g, w, h);
        drawLegend(g, h);
        drawHover(g, w, h);
    }

    private void drawEmptyMessage(Graphics2D g) {
        String line1 = "Nothing to show yet";
        String line2 = "Tick a run on the left or run SimpleRunTimeAnalysis to create CSV files.";
        g.setFont(Theme.CAPTION);
        g.setColor(Theme.TEXT);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(line1, (getWidth() - fm.stringWidth(line1)) / 2, getHeight() / 2 - 8);
        g.setFont(Theme.NORMAL);
        g.setColor(Theme.TEXT_DIM);
        fm = g.getFontMetrics();
        g.drawString(line2, (getWidth() - fm.stringWidth(line2)) / 2, getHeight() / 2 + 16);
    }

    private List<Double> yTicks() {
        List<Double> ticks = new ArrayList<>();
        if (logScale) {
            ticks.add(0.0);
            double v = 1;
            while (v <= yMax) {
                ticks.add(v);
                v = v * 10;
            }
        } else {
            for (int i = 0; i <= 5; i++) {
                ticks.add(yMax / 5 * i);
            }
        }
        return ticks;
    }

    private void drawGrid(Graphics2D g, int w, int h) {
        g.setFont(Theme.SMALL);
        FontMetrics fm = g.getFontMetrics();
        g.setStroke(new BasicStroke(1f));

        for (double t : yTicks()) {
            int y = (int) toY(t, h);
            g.setColor(Theme.GRID);
            g.drawLine(left, y, left + w, y);
            g.setColor(Theme.TEXT_DIM);
            String s = format(t);
            g.drawString(s, left - 8 - fm.stringWidth(s), y + 4);
        }

        for (int i = 0; i <= 5; i++) {
            double value = xMax / 5 * i;
            int x = (int) toX(value, w);
            g.setColor(Theme.GRID);
            g.drawLine(x, top, x, top + h);
            g.setColor(Theme.TEXT_DIM);
            String s = format(value);
            g.drawString(s, x - fm.stringWidth(s) / 2, top + h + 18);
        }

        g.setColor(Theme.BORDER);
        g.drawLine(left, top + h, left + w, top + h);
        g.drawLine(left, top, left, top + h);

        g.setFont(Theme.NORMAL);
        g.setColor(Theme.TEXT_DIM);
        fm = g.getFontMetrics();
        String xTitle = "Array length";
        g.drawString(xTitle, left + (w - fm.stringWidth(xTitle)) / 2, getHeight() - 12);

        String yTitle = logScale ? "Time (ms, log scale)" : "Time (ms)";
        Graphics2D rotated = (Graphics2D) g.create();
        rotated.rotate(-Math.PI / 2);
        rotated.drawString(yTitle, -(top + h / 2 + fm.stringWidth(yTitle) / 2), 18);
        rotated.dispose();
    }

    private void drawLines(Graphics2D g, int w, int h) {
        for (Series s : series) {
            boolean hot = (s == hoverSeries);
            g.setColor(s.color);
            g.setStroke(new BasicStroke(hot ? 3.2f : 2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int i = 0; i < s.xs.length - 1; i++) {
                g.draw(new Line2D.Double(toX(s.xs[i], w), toY(s.ys[i], h),
                        toX(s.xs[i + 1], w), toY(s.ys[i + 1], h)));
            }

            if (showPoints || s.xs.length == 1) {
                for (int i = 0; i < s.xs.length; i++) {
                    double px = toX(s.xs[i], w);
                    double py = toY(s.ys[i], h);
                    g.fill(new Ellipse2D.Double(px - 3.5, py - 3.5, 7, 7));
                }
            }
        }
    }

    private void drawLegend(Graphics2D g, int h) {
        g.setFont(Theme.SMALL);
        FontMetrics fm = g.getFontMetrics();
        int rowHeight = 18;

        int maxRows = Math.max(1, (h - 30) / rowHeight);
        int rows = Math.min(series.size(), maxRows);

        String[] texts = new String[rows];
        int widest = 0;
        for (int i = 0; i < rows; i++) {
            Series s = series.get(i);
            texts[i] = s.algorithm + "   " + s.runName + "   " + format(s.lastY()) + " ms";
            widest = Math.max(widest, fm.stringWidth(texts[i]));
        }

        int boxW = widest + 40;
        int boxH = rows * rowHeight + 12;
        int x = left + 14;
        int y = top + 10;

        g.setColor(new Color(20, 22, 26, 220));
        g.fillRoundRect(x, y, boxW, boxH, 8, 8);
        g.setColor(Theme.BORDER);
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, boxW, boxH, 8, 8);

        for (int i = 0; i < rows; i++) {
            int rowY = y + 6 + i * rowHeight;
            g.setColor(series.get(i).color);
            g.fillRoundRect(x + 10, rowY + 4, 12, 8, 3, 3);
            g.setColor(Theme.TEXT);
            g.drawString(texts[i], x + 30, rowY + 13);
        }
    }

    private void findHover(int mx, int my) {
        int w = getWidth() - left - right;
        int h = getHeight() - top - bottom;

        Series best = null;
        int bestIndex = -1;
        double bestDist = 14;

        for (Series s : series) {
            for (int i = 0; i < s.xs.length; i++) {
                double dx = toX(s.xs[i], w) - mx;
                double dy = toY(s.ys[i], h) - my;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = s;
                    bestIndex = i;
                }
            }
        }

        if (best != hoverSeries || bestIndex != hoverIndex) {
            hoverSeries = best;
            hoverIndex = bestIndex;
            repaint();
        }
    }

    private void drawHover(Graphics2D g, int w, int h) {
        if (hoverSeries == null) {
            return;
        }
        int px = (int) toX(hoverSeries.xs[hoverIndex], w);
        int py = (int) toY(hoverSeries.ys[hoverIndex], h);

        g.setColor(hoverSeries.color);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(px - 7, py - 7, 14, 14);

        String[] lines = {
            hoverSeries.algorithm + "   " + hoverSeries.runName,
            "length   " + format(hoverSeries.xs[hoverIndex]),
            "time   " + format(hoverSeries.ys[hoverIndex]) + " ms"
        };

        g.setFont(Theme.SMALL);
        FontMetrics fm = g.getFontMetrics();
        int widest = 0;
        for (String line : lines) {
            widest = Math.max(widest, fm.stringWidth(line));
        }
        int boxW = widest + 20;
        int boxH = lines.length * 16 + 12;

        int bx = px + 14;
        if (bx + boxW > getWidth() - 6) {
            bx = px - 14 - boxW;
        }
        int by = py - boxH - 10;
        if (by < 4) {
            by = py + 14;
        }

        g.setColor(Theme.PANEL_LIGHT);
        g.fillRoundRect(bx, by, boxW, boxH, 8, 8);
        g.setColor(Theme.BORDER);
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(bx, by, boxW, boxH, 8, 8);

        for (int i = 0; i < lines.length; i++) {
            g.setColor(i == 0 ? hoverSeries.color : Theme.TEXT);
            g.drawString(lines[i], bx + 10, by + 20 + i * 16);
        }
    }
}
