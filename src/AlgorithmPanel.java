import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlgorithmPanel extends JPanel {

    private AlgorithmRenderer renderer = new AlgorithmRenderer();

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> list = new JList<>(listModel);
    private Stage stage = new Stage();
    private JButton playButton;
    private Timer timer;

    private String current = null;
    private int tick = 0;
    private int hold = 0;
    private boolean finished = false;
    private boolean playing = true;
    private boolean updatingList = false;

    public AlgorithmPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        add(buildList(), BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(Theme.BACKGROUND);
        right.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        right.add(buildControls(), BorderLayout.NORTH);
        right.add(stage, BorderLayout.CENTER);
        add(right, BorderLayout.CENTER);

        timer = new Timer(600, e -> {
            if (!playing) {
                return;
            }
            if (finished) {
                // stay on the last step for a moment, then start again
                hold++;
                if (hold > 6) {
                    tick = 0;
                    hold = 0;
                }
            } else {
                tick++;
            }
            stage.repaint();
        });
        timer.start();
    }

    private JScrollPane buildList() {
        list.setBackground(Theme.PANEL);
        list.setForeground(Theme.TEXT);
        list.setSelectionBackground(Theme.PANEL_LIGHT);
        list.setSelectionForeground(Theme.ACCENT);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                                                          boolean selected, boolean focus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(l, value, index, selected, false);
                label.setFont(Theme.NORMAL);
                label.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
                if (!selected && !hasRenderer(String.valueOf(value))) {
                    label.setForeground(Theme.TEXT_DIM);
                }
                return label;
            }
        });
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !updatingList) {
                selectionChanged();
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        Theme.styleScroll(scroll);
        scroll.setPreferredSize(new Dimension(210, 0));
        return scroll;
    }

    private JPanel buildControls() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setBackground(Theme.BACKGROUND);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        playButton = Theme.button("Pause");
        playButton.addActionListener(e -> {
            playing = !playing;
            playButton.setText(playing ? "Pause" : "Play");
        });

        JButton back = Theme.button("Back");
        back.addActionListener(e -> {
            pause();
            if (tick > 0) {
                tick--;
            }
            hold = 0;
            stage.repaint();
        });

        JButton next = Theme.button("Next step");
        next.addActionListener(e -> {
            pause();
            if (!finished) {
                tick++;
            }
            stage.repaint();
        });

        JButton restart = Theme.button("Restart");
        restart.addActionListener(e -> restart());

        JLabel speedLabel = new JLabel("Speed");
        speedLabel.setForeground(Theme.TEXT_DIM);
        speedLabel.setFont(Theme.NORMAL);

        JSlider speed = new JSlider(1, 10, 5);
        speed.setOpaque(false);
        speed.setFocusable(false);
        speed.setPreferredSize(new Dimension(140, 24));
        speed.addChangeListener(e -> timer.setDelay(1100 - speed.getValue() * 100));

        bar.add(playButton);
        bar.add(back);
        bar.add(next);
        bar.add(restart);
        bar.add(spacer());
        bar.add(speedLabel);
        bar.add(speed);
        return bar;
    }

    private Component spacer() {
        JPanel gap = new JPanel();
        gap.setOpaque(false);
        gap.setPreferredSize(new Dimension(16, 1));
        return gap;
    }

    private void pause() {
        playing = false;
        playButton.setText("Play");
    }

    private void restart() {
        tick = 0;
        hold = 0;
        stage.repaint();
    }

    private void selectionChanged() {
        String selected = list.getSelectedValue();
        if (selected != null && !selected.equals(current)) {
            current = selected;
            restart();
        }
    }

    // names from the CSV files get merged with the names of the render methods
    public void setNames(List<String> csvNames) {
        List<String> names = renderMethodNames();
        for (String name : csvNames) {
            if (!names.contains(name)) {
                names.add(name);
            }
        }
        Collections.sort(names);

        String keep = list.getSelectedValue();
        updatingList = true;
        listModel.clear();
        for (String name : names) {
            listModel.addElement(name);
        }
        if (keep != null && names.contains(keep)) {
            list.setSelectedValue(keep, true);
        } else if (!names.isEmpty()) {
            list.setSelectedIndex(0);
        }
        updatingList = false;
        selectionChanged();
    }

    // every public method called render<Name> in AlgorithmRenderer is an algorithm
    private List<String> renderMethodNames() {
        List<String> names = new ArrayList<>();
        for (Method m : AlgorithmRenderer.class.getDeclaredMethods()) {
            String name = m.getName();
            if (name.startsWith("render") && name.length() > 6 && Modifier.isPublic(m.getModifiers())) {
                names.add(name.substring(6));
            }
        }
        return names;
    }

    private boolean hasRenderer(String name) {
        return renderMethodNames().contains(name);
    }

    // the area where the infographic is drawn
    private class Stage extends JPanel {

        Stage() {
            setBackground(Theme.PANEL);
            setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        }

        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (current == null) {
                g.dispose();
                return;
            }

            Rectangle area = new Rectangle(30, 26, getWidth() - 60, getHeight() - 52);
            try {
                Method m = AlgorithmRenderer.class.getMethod("render" + current,
                        Graphics2D.class, Rectangle.class, int.class);
                finished = (Boolean) m.invoke(renderer, g, area, tick);
            } catch (NoSuchMethodException e) {
                finished = false;
                drawMissing(g);
            } catch (Exception e) {
                finished = false;
                e.printStackTrace();
            }
            g.dispose();
        }

        private void drawMissing(Graphics2D g) {
            String line1 = "No infographic for " + current + " yet";
            String line2 = "Add a public method render" + current + "(Graphics2D g, Rectangle area, int tick)";
            String line3 = "to AlgorithmRenderer and it shows up here.";

            g.setFont(Theme.CAPTION);
            g.setColor(Theme.TEXT);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(line1, (getWidth() - fm.stringWidth(line1)) / 2, getHeight() / 2 - 16);

            g.setFont(Theme.NORMAL);
            g.setColor(Theme.TEXT_DIM);
            fm = g.getFontMetrics();
            g.drawString(line2, (getWidth() - fm.stringWidth(line2)) / 2, getHeight() / 2 + 10);
            g.drawString(line3, (getWidth() - fm.stringWidth(line3)) / 2, getHeight() / 2 + 30);
        }
    }
}
