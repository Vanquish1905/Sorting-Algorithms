import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Theme {

    public static final Color BACKGROUND = new Color(20, 22, 26);
    public static final Color PANEL = new Color(28, 31, 36);
    public static final Color PANEL_LIGHT = new Color(40, 44, 51);
    public static final Color BORDER = new Color(52, 57, 65);
    public static final Color GRID = new Color(42, 46, 53);
    public static final Color TEXT = new Color(225, 228, 232);
    public static final Color TEXT_DIM = new Color(139, 147, 158);
    public static final Color ACCENT = new Color(232, 163, 61);

    // colors for the lines in the graph
    public static final Color[] LINE_COLORS = {
        new Color(232, 163, 61),
        new Color(63, 184, 166),
        new Color(229, 105, 79),
        new Color(90, 167, 224),
        new Color(166, 194, 79),
        new Color(217, 115, 155),
        new Color(201, 185, 138),
        new Color(155, 135, 214)
    };

    // colors for the bars in the "how it works" view, the index is the mark code
    // 0 normal, 1 compared, 2 pivot / smallest / key, 3 sorted, 4 left or smaller, 5 right or bigger
    public static final Color[] MARK_COLORS = {
        new Color(62, 70, 82),
        new Color(232, 163, 61),
        new Color(229, 105, 79),
        new Color(63, 184, 166),
        new Color(90, 167, 224),
        new Color(217, 115, 155)
    };

    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font CAPTION = new Font("SansSerif", Font.BOLD, 16);
    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 22);

    public static void applyDefaults() {
        UIManager.put("ToolTip.background", PANEL_LIGHT);
        UIManager.put("ToolTip.foreground", TEXT);
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("Panel.background", PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("FileChooser.background", PANEL);
    }

    public static JButton button(String text) {
        JButton b = new JButton(text);
        b.setFont(NORMAL);
        b.setBackground(PANEL_LIGHT);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(BORDER);
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(PANEL_LIGHT);
            }
        });
        return b;
    }

    public static JCheckBox checkbox(String text, Color color, boolean selected) {
        JCheckBox box = new JCheckBox(text);
        box.setIcon(new Swatch(color, false));
        box.setSelectedIcon(new Swatch(color, true));
        box.setSelected(selected);
        box.setOpaque(false);
        box.setForeground(TEXT);
        box.setFont(NORMAL);
        box.setFocusPainted(false);
        box.setIconTextGap(8);
        return box;
    }

    public static void styleScroll(JScrollPane scroll) {
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(PANEL);
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
    }

    // small square used instead of the normal checkbox icon
    private static class Swatch implements Icon {
        private Color color;
        private boolean filled;

        Swatch(Color color, boolean filled) {
            this.color = color;
            this.filled = filled;
        }

        public int getIconWidth() {
            return 14;
        }

        public int getIconHeight() {
            return 14;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (filled) {
                g2.setColor(color);
                g2.fillRoundRect(x, y, 14, 14, 4, 4);
            } else {
                g2.setColor(BORDER.brighter());
                g2.drawRoundRect(x, y, 13, 13, 4, 4);
            }
            g2.dispose();
        }
    }
}
