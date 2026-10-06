import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends JFrame {

    private File folder = new File("results");
    private List<Series> allSeries = new ArrayList<>();
    private Map<String, Color> colors = new HashMap<>();
    private Map<String, Boolean> shown = new HashMap<>();
    private String lastSignature = "";

    private GraphPanel graph = new GraphPanel();
    private AlgorithmPanel algorithmPanel = new AlgorithmPanel();
    private JPanel runList = new JPanel();
    private JLabel folderLabel = new JLabel();
    private JCheckBox watchBox;
    private JButton graphTab;
    private JButton infoTab;
    private CardLayout cards = new CardLayout();
    private JPanel content = new JPanel(cards);

    public Dashboard() {
        super("Sort Runtime Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(920, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        content.add(buildGraphPage(), "graph");
        content.add(algorithmPanel, "info");
        add(content, BorderLayout.CENTER);

        showPage("graph");
        reload();
        startWatcher();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        header.setBackground(Theme.PANEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        JLabel title = new JLabel("Sort Runtime Viewer");
        title.setFont(Theme.CAPTION);
        title.setForeground(Theme.TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 24));

        graphTab = Theme.button("Graph");
        graphTab.addActionListener(e -> showPage("graph"));
        infoTab = Theme.button("How it works");
        infoTab.addActionListener(e -> showPage("info"));

        header.add(title);
        header.add(graphTab);
        header.add(infoTab);
        return header;
    }

    private void showPage(String name) {
        cards.show(content, name);
        graphTab.setForeground(name.equals("graph") ? Theme.ACCENT : Theme.TEXT);
        infoTab.setForeground(name.equals("info") ? Theme.ACCENT : Theme.TEXT);
    }

    private JPanel buildGraphPage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Theme.BACKGROUND);

        // left side: control panel
        JPanel controls = new JPanel(new BorderLayout(0, 10));
        controls.setBackground(Theme.BACKGROUND);
        controls.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 0));
        controls.setPreferredSize(new Dimension(280, 0));

        JPanel runsTop = new JPanel(new BorderLayout(0, 8));
        runsTop.setOpaque(false);
        runsTop.add(sectionLabel("RUNS"), BorderLayout.NORTH);

        JPanel quick = new JPanel(new GridLayout(1, 3, 6, 0));
        quick.setOpaque(false);
        JButton all = Theme.button("All");
        JButton none = Theme.button("None");
        JButton newest = Theme.button("Newest");
        newest.setToolTipText("Only the newest run of every algorithm");
        all.addActionListener(e -> setAllVisible(true));
        none.addActionListener(e -> setAllVisible(false));
        newest.addActionListener(e -> showNewestOnly());
        quick.add(all);
        quick.add(none);
        quick.add(newest);
        runsTop.add(quick, BorderLayout.SOUTH);

        runList.setLayout(new BoxLayout(runList, BoxLayout.Y_AXIS));
        runList.setBackground(Theme.PANEL);
        runList.setBorder(BorderFactory.createEmptyBorder(6, 10, 10, 10));
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(Theme.PANEL);
        holder.add(runList, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(holder);
        Theme.styleScroll(scroll);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        JCheckBox logBox = Theme.checkbox("Log scale (time)", Theme.ACCENT, false);
        logBox.addActionListener(e -> graph.setLogScale(logBox.isSelected()));
        JCheckBox pointsBox = Theme.checkbox("Show points", Theme.ACCENT, true);
        pointsBox.addActionListener(e -> graph.setShowPoints(pointsBox.isSelected()));
        watchBox = Theme.checkbox("Watch folder for new files", Theme.ACCENT, true);

        bottom.add(sectionLabel("DISPLAY"));
        bottom.add(space(6));
        bottom.add(leftAligned(logBox));
        bottom.add(leftAligned(pointsBox));
        bottom.add(leftAligned(watchBox));
        bottom.add(space(14));
        bottom.add(sectionLabel("FOLDER"));
        bottom.add(space(6));

        folderLabel.setForeground(Theme.TEXT_DIM);
        folderLabel.setFont(Theme.SMALL);
        bottom.add(leftAligned(folderLabel));
        bottom.add(space(8));

        JPanel folderButtons = new JPanel(new GridLayout(1, 2, 6, 0));
        folderButtons.setOpaque(false);
        JButton choose = Theme.button("Choose...");
        choose.addActionListener(e -> chooseFolder());
        JButton refresh = Theme.button("Reload");
        refresh.addActionListener(e -> reload());
        folderButtons.add(choose);
        folderButtons.add(refresh);
        bottom.add(leftAligned(folderButtons));

        controls.add(runsTop, BorderLayout.NORTH);
        controls.add(scroll, BorderLayout.CENTER);
        controls.add(bottom, BorderLayout.SOUTH);

        // right side: the graph
        JPanel graphHolder = new JPanel(new BorderLayout());
        graphHolder.setBackground(Theme.BACKGROUND);
        graphHolder.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        graph.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        graphHolder.add(graph, BorderLayout.CENTER);

        page.add(controls, BorderLayout.WEST);
        page.add(graphHolder, BorderLayout.CENTER);
        return page;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.SMALL);
        label.setForeground(Theme.TEXT_DIM);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private Component space(int height) {
        JPanel space = new JPanel();
        space.setOpaque(false);
        space.setPreferredSize(new Dimension(1, height));
        space.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        space.setAlignmentX(Component.LEFT_ALIGNMENT);
        return space;
    }

    private JComponent leftAligned(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }


    private void reload() {
        lastSignature = signature();
        List<Series> loaded = ResultsLoader.loadAll(folder);

        for (Series s : loaded) {
            if (!colors.containsKey(s.label)) {
                // first time we see this file: give it a color and show it right away
                colors.put(s.label, Theme.LINE_COLORS[colors.size() % Theme.LINE_COLORS.length]);
                shown.put(s.label, true);
            }
            s.color = colors.get(s.label);
            s.visible = shown.get(s.label);
        }
        allSeries = loaded;

        String path = folder.getAbsolutePath();
        folderLabel.setText("<html><body style='width:230px'>" + path + "</body></html>");

        rebuildRunList();
        updateGraph();

        List<String> names = new ArrayList<>();
        for (Series s : allSeries) {
            if (!names.contains(s.algorithm)) {
                names.add(s.algorithm);
            }
        }
        algorithmPanel.setNames(names);
    }

    // file names, sizes and change dates in one string, if it changes something happened in the folder
    private String signature() {
        File[] files = folder.listFiles();
        if (files == null) {
            return "";
        }
        Arrays.sort(files);
        StringBuilder sb = new StringBuilder();
        for (File f : files) {
            if (f.getName().toLowerCase().endsWith(".csv")) {
                sb.append(f.getName()).append(f.lastModified()).append(f.length()).append("|");
            }
        }
        return sb.toString();
    }

    private void startWatcher() {
        Timer timer = new Timer(1500, e -> {
            if (watchBox.isSelected() && !signature().equals(lastSignature)) {
                reload();
            }
        });
        timer.start();
    }

    private void chooseFolder() {
        JFileChooser chooser = new JFileChooser(folder.getAbsoluteFile());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            folder = chooser.getSelectedFile();
            reload();
        }
    }


    private void rebuildRunList() {
        runList.removeAll();

        if (allSeries.isEmpty()) {
            JLabel empty = new JLabel("<html><body style='width:200px'>No CSV files found in this folder.</body></html>");
            empty.setForeground(Theme.TEXT_DIM);
            empty.setFont(Theme.NORMAL);
            runList.add(empty);
        }

        // group the runs by algorithm
        Map<String, List<Series>> groups = new LinkedHashMap<>();
        for (Series s : allSeries) {
            if (!groups.containsKey(s.algorithm)) {
                groups.put(s.algorithm, new ArrayList<>());
            }
            groups.get(s.algorithm).add(s);
        }

        for (String name : groups.keySet()) {
            JLabel header = new JLabel(name);
            header.setFont(Theme.BOLD);
            header.setForeground(Theme.TEXT);
            header.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));
            header.setAlignmentX(Component.LEFT_ALIGNMENT);
            runList.add(header);

            for (Series s : groups.get(name)) {
                JCheckBox box = Theme.checkbox(s.runName, s.color, s.visible);
                box.setAlignmentX(Component.LEFT_ALIGNMENT);
                box.addActionListener(e -> {
                    s.visible = box.isSelected();
                    shown.put(s.label, s.visible);
                    updateGraph();
                });
                runList.add(box);
            }
        }

        runList.revalidate();
        runList.repaint();
    }

    private void updateGraph() {
        List<Series> visible = new ArrayList<>();
        for (Series s : allSeries) {
            if (s.visible) {
                visible.add(s);
            }
        }
        graph.setSeries(visible);
    }

    private void setAllVisible(boolean value) {
        for (Series s : allSeries) {
            s.visible = value;
            shown.put(s.label, value);
        }
        rebuildRunList();
        updateGraph();
    }

    private void showNewestOnly() {
        Map<String, Series> newest = new HashMap<>();
        for (Series s : allSeries) {
            Series best = newest.get(s.algorithm);
            if (best == null || s.modified >= best.modified) {
                newest.put(s.algorithm, s);
            }
        }
        for (Series s : allSeries) {
            s.visible = (newest.get(s.algorithm) == s);
            shown.put(s.label, s.visible);
        }
        rebuildRunList();
        updateGraph();
    }


    public static void main(String[] args) {
        Theme.applyDefaults();
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
