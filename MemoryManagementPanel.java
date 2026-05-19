import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class MemoryManagementPanel extends JPanel {

  private final MainFrame frame;

  private final JTextField referenceField =
    new JTextField("7 0 1 2 0 3 0 4 2 3 0 3 2");

  private final JTextField frameField = new JTextField("3");

  private final DefaultTableModel frameModel = new DefaultTableModel();
  private final DefaultTableModel pageTableModel = new DefaultTableModel(
    new String[] { "Page", "Frame", "Valid Bit", "Last Used" },
    0
  );

  private final JTable frameTable = new JTable(frameModel);
  private final JTable pageTable = new JTable(pageTableModel);

  private final JTextArea logArea = new JTextArea();

  private final JLabel faultMetric = new JLabel("0");
  private final JLabel hitMetric = new JLabel("0");
  private final JLabel hitRatioMetric = new JLabel("0.00%");
  private final JLabel missRatioMetric = new JLabel("0.00%");
  private final JLabel memoryUtilMetric = new JLabel("0.00%");

  public MemoryManagementPanel(MainFrame frame) {
    this.frame = frame;

    setOpaque(false);
    setLayout(new BorderLayout());

    JPanel content = new JPanel();
    content.setOpaque(false);
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBorder(new EmptyBorder(28, 34, 28, 34));

    JPanel header = buildHeader();
    header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

    JPanel main = buildMainArea();
    main.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

    JPanel metrics = buildMetricsBar();
    metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
    metrics.setPreferredSize(new Dimension(1000, 82));

    content.add(header);
    content.add(Box.createVerticalStrut(22));
    content.add(main);
    content.add(Box.createVerticalStrut(16));
    content.add(metrics);

    JScrollPane scrollPane = new JScrollPane(
      content,
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );

    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setOpaque(false);
    scrollPane.getVerticalScrollBar().setUnitIncrement(18);

    add(scrollPane, BorderLayout.CENTER);

    resetModule();
  }

  private JPanel buildHeader() {
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);

    JPanel titleBox = new JPanel();
    titleBox.setOpaque(false);
    titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

    JLabel title = new JLabel("Memory Management");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 30));

    JLabel subtitle = new JLabel(
      "Simulate paging, LRU page replacement, frame allocation, page table updates, and page-fault analysis"
    );
    subtitle.setForeground(new Color(214, 225, 245));
    subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));

    titleBox.add(title);
    titleBox.add(Box.createVerticalStrut(8));
    titleBox.add(subtitle);

    RoundedButton back = new RoundedButton(
      "Back to Menu",
      new Color(36, 58, 99),
      new Color(25, 40, 72)
    );
    back.setPreferredSize(new Dimension(170, 60));
    back.addActionListener(e -> frame.showMenu());

    header.add(titleBox, BorderLayout.WEST);
    header.add(back, BorderLayout.EAST);

    return header;
  }

  private JPanel buildMainArea() {
    JPanel main = new JPanel(new BorderLayout(20, 20));
    main.setOpaque(false);

    main.add(buildLeftControls(), BorderLayout.WEST);
    main.add(buildTablesPanel(), BorderLayout.CENTER);
    main.add(buildLogPanel(), BorderLayout.EAST);

    return main;
  }

  private JPanel buildLeftControls() {
    RoundedPanel panel = new RoundedPanel(28, new Color(255, 255, 255, 24));
    panel.setOpaque(false);
    panel.setPreferredSize(new Dimension(280, 430));
    panel.setMaximumSize(new Dimension(280, 430));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 18, 20, 18));

    JLabel title = new JLabel("Memory Controls");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 21));
    title.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel refLabel = label("Page Reference String");
    referenceField.setMaximumSize(new Dimension(230, 32));
    referenceField.setPreferredSize(new Dimension(230, 32));

    JLabel frameLabel = label("Number of Frames");
    frameField.setMaximumSize(new Dimension(230, 32));
    frameField.setPreferredSize(new Dimension(230, 32));

    JLabel algorithmLabel = label("Replacement Policy");
    JLabel algorithmValue = new JLabel("LRU");
    algorithmValue.setForeground(Color.WHITE);
    algorithmValue.setFont(new Font("SansSerif", Font.BOLD, 16));
    algorithmValue.setAlignmentX(Component.CENTER_ALIGNMENT);

    RoundedButton run = sideButton("Run Simulation");
    run.addActionListener(e -> runSimulation());

    RoundedButton preset = sideButton("Load Preset");
    preset.addActionListener(e -> loadPreset());

    RoundedButton reset = sideButton("Reset Module");
    reset.addActionListener(e -> resetModule());

    panel.add(title);
    panel.add(Box.createVerticalStrut(18));

    panel.add(refLabel);
    panel.add(Box.createVerticalStrut(6));
    panel.add(referenceField);
    panel.add(Box.createVerticalStrut(14));

    panel.add(frameLabel);
    panel.add(Box.createVerticalStrut(6));
    panel.add(frameField);
    panel.add(Box.createVerticalStrut(14));

    panel.add(algorithmLabel);
    panel.add(Box.createVerticalStrut(6));
    panel.add(algorithmValue);

    panel.add(Box.createVerticalStrut(22));
    panel.add(run);
    panel.add(Box.createVerticalStrut(12));
    panel.add(preset);
    panel.add(Box.createVerticalStrut(12));
    panel.add(reset);
    panel.add(Box.createVerticalGlue());

    return panel;
  }

  private JLabel label(String text) {
    JLabel label = new JLabel(text);
    label.setForeground(new Color(220, 230, 250));
    label.setFont(new Font("SansSerif", Font.BOLD, 14));
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
    return label;
  }

  private RoundedButton sideButton(String text) {
    RoundedButton button = new RoundedButton(
      text,
      new Color(88, 153, 255),
      new Color(58, 112, 218)
    );
    button.setMaximumSize(new Dimension(230, 42));
    button.setPreferredSize(new Dimension(230, 42));
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    return button;
  }

  private JPanel buildTablesPanel() {
    JPanel wrapper = new JPanel();
    wrapper.setOpaque(false);
    wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

    JPanel frameCard = tableCard("Frame Table - Step by Step LRU Trace", frameTable);
    JPanel pageCard = tableCard("Page Table View", pageTable);

    frameCard.setAlignmentX(Component.LEFT_ALIGNMENT);
    pageCard.setAlignmentX(Component.LEFT_ALIGNMENT);

    wrapper.add(frameCard);
    wrapper.add(Box.createVerticalStrut(18));
    wrapper.add(pageCard);

    return wrapper;
  }

  private JPanel tableCard(String title, JTable table) {
    RoundedPanel card = new RoundedPanel(28, new Color(255, 255, 255, 24));
    card.setOpaque(false);
    card.setLayout(new BorderLayout(10, 10));
    card.setBorder(new EmptyBorder(18, 18, 18, 18));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

    JLabel label = new JLabel(title);
    label.setForeground(Color.WHITE);
    label.setFont(new Font("SansSerif", Font.BOLD, 18));

    table.setRowHeight(28);
    table.setFont(new Font("SansSerif", Font.PLAIN, 13));
    table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
    table.setFillsViewportHeight(false);

    JScrollPane scroll = new JScrollPane(
      table,
      JScrollPane.VERTICAL_SCROLLBAR_NEVER,
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );
    scroll.setBorder(BorderFactory.createEmptyBorder());

    card.add(label, BorderLayout.NORTH);
    card.add(scroll, BorderLayout.CENTER);

    return card;
  }

  private JPanel buildLogPanel() {
    RoundedPanel panel = new RoundedPanel(28, new Color(255, 255, 255, 24));
    panel.setOpaque(false);
    panel.setPreferredSize(new Dimension(390, 430));
    panel.setMaximumSize(new Dimension(390, 430));
    panel.setLayout(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(18, 18, 18, 18));

    JLabel title = new JLabel("Memory Event Log");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 20));

    logArea.setEditable(false);
    logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
    logArea.setBackground(new Color(8, 18, 46));
    logArea.setForeground(new Color(220, 235, 255));
    logArea.setLineWrap(true);
    logArea.setWrapStyleWord(true);
    logArea.setText("[LOG] Memory Management module loaded.\n");

    JScrollPane scroll = new JScrollPane(
      logArea,
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    scroll.setBorder(BorderFactory.createEmptyBorder());

    panel.add(title, BorderLayout.NORTH);
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
  }

  private JPanel buildMetricsBar() {
    RoundedPanel bar = new RoundedPanel(24, new Color(255, 255, 255, 22));
    bar.setOpaque(false);
    bar.setLayout(new GridLayout(1, 5, 10, 0));
    bar.setBorder(new EmptyBorder(14, 16, 14, 16));

    bar.add(metricCard("Page Faults", faultMetric));
    bar.add(metricCard("Hits", hitMetric));
    bar.add(metricCard("Hit Ratio", hitRatioMetric));
    bar.add(metricCard("Miss Ratio", missRatioMetric));
    bar.add(metricCard("Memory Util", memoryUtilMetric));

    return bar;
  }

  private JPanel metricCard(String title, JLabel valueLabel) {
    JPanel card = new JPanel();
    card.setOpaque(false);
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

    valueLabel.setForeground(Color.WHITE);
    valueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
    valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setForeground(new Color(210, 225, 250));
    titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    card.add(valueLabel);
    card.add(Box.createVerticalStrut(4));
    card.add(titleLabel);

    return card;
  }

  private void loadPreset() {
    referenceField.setText("7 0 1 2 0 3 0 4 2 3 0 3 2");
    frameField.setText("3");
    log("[LOG] Preset loaded: reference string and 3 frames.");
  }

  private void runSimulation() {
    String referenceText = referenceField.getText().trim();

    if (referenceText.isEmpty()) {
      showError("Reference string cannot be empty.");
      return;
    }

    int frameCount;

    try {
      frameCount = parsePositiveInt(frameField.getText(), "Number of Frames");
    } catch (IllegalArgumentException ex) {
      showError(ex.getMessage());
      return;
    }

    String[] parts = referenceText.split("\\s+");
    int[] references = new int[parts.length];

    try {
      for (int i = 0; i < parts.length; i++) {
        references[i] = parseNonNegativeInt(parts[i], "Page reference");
      }
    } catch (IllegalArgumentException ex) {
      showError("Reference string must contain only non-negative page numbers.");
      return;
    }

    setupFrameTableColumns(frameCount);
    pageTableModel.setRowCount(0);
    logArea.setText("");

    ArrayList<Integer> frames = new ArrayList<>();
    HashMap<Integer, Integer> lastUsed = new HashMap<>();

    int hits = 0;
    int faults = 0;

    log("[LOG] LRU paging simulation started.");
    log("[LOG] Frames available: " + frameCount);
    log("[LOG] Reference string: " + referenceText);
    log("");

    for (int step = 0; step < references.length; step++) {
      int page = references[step];
      boolean hit = frames.contains(page);

      if (hit) {
        hits++;
        log("[STEP " + (step + 1) + "] Page " + page + " → HIT");
      } else {
        faults++;

        if (frames.size() < frameCount) {
          frames.add(page);
          log("[STEP " + (step + 1) + "] Page " + page + " → FAULT, loaded into free frame.");
        } else {
          int victim = findLRUVictim(frames, lastUsed);
          int victimIndex = frames.indexOf(victim);
          frames.set(victimIndex, page);

          log(
            "[STEP " + (step + 1) + "] Page " + page +
            " → FAULT, replaced page " + victim + " using LRU."
          );
        }
      }

      lastUsed.put(page, step);

      Object[] row = new Object[frameCount + 3];
      row[0] = step + 1;
      row[1] = page;

      for (int f = 0; f < frameCount; f++) {
        if (f < frames.size()) {
          row[f + 2] = frames.get(f);
        } else {
          row[f + 2] = "";
        }
      }

      row[frameCount + 2] = hit ? "Hit" : "Fault";
      frameModel.addRow(row);

      refreshPageTable(frames, lastUsed);
    }

    double hitRatio = references.length == 0 ? 0 : (hits * 100.0) / references.length;
    double missRatio = references.length == 0 ? 0 : (faults * 100.0) / references.length;
    double memoryUtil = frameCount == 0 ? 0 : (frames.size() * 100.0) / frameCount;

    faultMetric.setText(String.valueOf(faults));
    hitMetric.setText(String.valueOf(hits));
    hitRatioMetric.setText(String.format("%.2f%%", hitRatio));
    missRatioMetric.setText(String.format("%.2f%%", missRatio));
    memoryUtilMetric.setText(String.format("%.2f%%", memoryUtil));

    refreshTableHeights();

    log("");
    log("[LOG] Simulation completed.");
  }

  private void setupFrameTableColumns(int frameCount) {
    frameModel.setColumnCount(0);
    frameModel.setRowCount(0);

    frameModel.addColumn("Step");
    frameModel.addColumn("Page");

    for (int i = 1; i <= frameCount; i++) {
      frameModel.addColumn("Frame " + i);
    }

    frameModel.addColumn("Status");
  }

  private int findLRUVictim(ArrayList<Integer> frames, HashMap<Integer, Integer> lastUsed) {
    int victim = frames.get(0);
    int oldest = lastUsed.getOrDefault(victim, -1);

    for (int page : frames) {
      int usedAt = lastUsed.getOrDefault(page, -1);

      if (usedAt < oldest) {
        oldest = usedAt;
        victim = page;
      }
    }

    return victim;
  }

  private void refreshPageTable(ArrayList<Integer> frames, HashMap<Integer, Integer> lastUsed) {
    pageTableModel.setRowCount(0);

    for (int i = 0; i < frames.size(); i++) {
      int page = frames.get(i);

      pageTableModel.addRow(new Object[] {
        page,
        i,
        1,
        lastUsed.getOrDefault(page, -1)
      });
    }
  }

  private void refreshTableHeights() {
    adjustTableHeight(frameTable);
    adjustTableHeight(pageTable);
    revalidate();
    repaint();
  }

  private void adjustTableHeight(JTable table) {
    int rows = Math.max(table.getRowCount(), 3);
    int height =
      table.getRowHeight() * rows +
      table.getTableHeader().getPreferredSize().height +
      8;

    table.setPreferredScrollableViewportSize(
      new Dimension(table.getPreferredSize().width, height)
    );
  }

  private void resetModule() {
    setupFrameTableColumns(3);
    pageTableModel.setRowCount(0);

    faultMetric.setText("0");
    hitMetric.setText("0");
    hitRatioMetric.setText("0.00%");
    missRatioMetric.setText("0.00%");
    memoryUtilMetric.setText("0.00%");

    logArea.setText("[LOG] Memory Management module reset.\n");

    refreshTableHeights();
  }

  private int parsePositiveInt(String value, String fieldName) {
    try {
      int parsed = Integer.parseInt(value.trim());

      if (parsed <= 0) {
        throw new IllegalArgumentException(fieldName + " must be greater than 0.");
      }

      return parsed;
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(fieldName + " must be a valid number.");
    }
  }

  private int parseNonNegativeInt(String value, String fieldName) {
    try {
      int parsed = Integer.parseInt(value.trim());

      if (parsed < 0) {
        throw new IllegalArgumentException(fieldName + " cannot be negative.");
      }

      return parsed;
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(fieldName + " must be a valid number.");
    }
  }

  private void log(String message) {
    logArea.append(message + "\n");
    logArea.setCaretPosition(logArea.getDocument().getLength());
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    GradientPaint gp = new GradientPaint(
      0,
      0,
      new Color(7, 16, 39),
      getWidth(),
      getHeight(),
      new Color(22, 55, 118)
    );

    g2.setPaint(gp);
    g2.fillRect(0, 0, getWidth(), getHeight());

    g2.setColor(new Color(255, 255, 255, 14));
    g2.fillOval(-100, 120, 280, 280);
    g2.fillOval(getWidth() - 260, getHeight() - 220, 260, 260);

    g2.dispose();
  }
}