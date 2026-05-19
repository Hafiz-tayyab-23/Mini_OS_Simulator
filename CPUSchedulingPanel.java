import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class CPUSchedulingPanel extends JPanel {

  private final MainFrame frame;

  private final String[] inputColumns = { "PID", "Mode", "Arrival Time", "Burst Time" };
  private final String[] resultColumns = { "PID", "AT", "BT", "CT", "TAT", "WT", "RT" };

  private final DefaultTableModel inputModel = new DefaultTableModel(inputColumns, 0);
  private final DefaultTableModel resultModel = new DefaultTableModel(resultColumns, 0);

  private final JTable inputTable = new JTable(inputModel);
  private final JTable resultTable = new JTable(resultModel);

  private final JComboBox<String> algorithmBox = new JComboBox<>(
    new String[] {
      "FCFS",
      "SJF",
      "Round Robin"
    }
  );

  private final JTextField quantumField = new JTextField("2");

  private final JTextArea executionLogArea = new JTextArea();
  private final GanttChartPanel ganttChartPanel = new GanttChartPanel();

  private final JLabel avgWTMetric = new JLabel("0.00");
  private final JLabel avgTATMetric = new JLabel("0.00");
  private final JLabel avgRTMetric = new JLabel("0.00");
  private final JLabel cpuUtilMetric = new JLabel("0.00%");
  private final JLabel throughputMetric = new JLabel("0.00");

  public CPUSchedulingPanel(MainFrame frame) {
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

    JPanel gantt = buildWideGanttChartCard();
    gantt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
    gantt.setPreferredSize(new Dimension(1000, 230));

    JPanel metrics = buildMetricsBar();
    metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
    metrics.setPreferredSize(new Dimension(1000, 82));

    content.add(header);
    content.add(Box.createVerticalStrut(22));
    content.add(main);
    content.add(Box.createVerticalStrut(22));
    content.add(gantt);
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

    refreshTableHeights();
  }

  private JPanel buildHeader() {
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);

    JPanel titleBox = new JPanel();
    titleBox.setOpaque(false);
    titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

    JLabel title = new JLabel("CPU Scheduling");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 30));

    JLabel subtitle = new JLabel(
      "Simulate FCFS, SJF, and Round Robin with graphical Gantt chart and performance metrics"
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
    main.add(buildRightPanel(), BorderLayout.EAST);

    return main;
  }

  private JPanel buildLeftControls() {
    RoundedPanel panel = new RoundedPanel(28, new Color(255, 255, 255, 24));
    panel.setOpaque(false);
    panel.setPreferredSize(new Dimension(250, 390));
    panel.setMaximumSize(new Dimension(250, 390));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 18, 20, 18));

    JLabel title = new JLabel("Scheduler Controls");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 21));
    title.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel algoLabel = label("Algorithm");
    algorithmBox.setMaximumSize(new Dimension(205, 30));
    algorithmBox.setPreferredSize(new Dimension(205, 30));

    JLabel quantumLabel = label("Time Quantum");
    quantumField.setMaximumSize(new Dimension(205, 30));
    quantumField.setPreferredSize(new Dimension(205, 30));

    RoundedButton addProcess = sideButton("Add Process");
    addProcess.addActionListener(e -> addProcessDialog());

    RoundedButton preset = sideButton("Load Preset");
    preset.addActionListener(e -> loadPreset());

    RoundedButton run = sideButton("Run Scheduling");
    run.addActionListener(e -> runScheduling());

    RoundedButton reset = sideButton("Reset Module");
    reset.addActionListener(e -> resetModule());

    panel.add(title);
    panel.add(Box.createVerticalStrut(16));
    panel.add(algoLabel);
    panel.add(Box.createVerticalStrut(5));
    panel.add(algorithmBox);
    panel.add(Box.createVerticalStrut(11));
    panel.add(quantumLabel);
    panel.add(Box.createVerticalStrut(5));
    panel.add(quantumField);
    panel.add(Box.createVerticalStrut(18));
    panel.add(addProcess);
    panel.add(Box.createVerticalStrut(10));
    panel.add(preset);
    panel.add(Box.createVerticalStrut(10));
    panel.add(run);
    panel.add(Box.createVerticalStrut(10));
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
    button.setMaximumSize(new Dimension(205, 42));
    button.setPreferredSize(new Dimension(205, 42));
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    return button;
  }

  private JPanel buildTablesPanel() {
    JPanel wrapper = new JPanel();
    wrapper.setOpaque(false);
    wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

    JPanel inputCard = tableCard("Input Process Table", inputTable);
    JPanel resultCard = tableCard("Scheduling Result Table", resultTable);

    inputCard.setAlignmentX(Component.LEFT_ALIGNMENT);
    resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);

    wrapper.add(inputCard);
    wrapper.add(Box.createVerticalStrut(18));
    wrapper.add(resultCard);

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

  private JPanel buildRightPanel() {
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setOpaque(false);
    rightPanel.setPreferredSize(new Dimension(390, 390));
    rightPanel.setMaximumSize(new Dimension(390, 390));

    rightPanel.add(buildExecutionLogCard(), BorderLayout.CENTER);

    return rightPanel;
  }

  private JPanel buildExecutionLogCard() {
    RoundedPanel panel = new RoundedPanel(28, new Color(255, 255, 255, 24));
    panel.setOpaque(false);
    panel.setLayout(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(18, 18, 18, 18));

    JLabel title = new JLabel("Execution Log");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 20));

    executionLogArea.setEditable(false);
    executionLogArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
    executionLogArea.setBackground(new Color(8, 18, 46));
    executionLogArea.setForeground(new Color(220, 235, 255));
    executionLogArea.setLineWrap(true);
    executionLogArea.setWrapStyleWord(true);
    executionLogArea.setText("[LOG] CPU Scheduling module loaded.\n");

    JScrollPane scroll = new JScrollPane(
      executionLogArea,
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    scroll.setBorder(BorderFactory.createEmptyBorder());

    panel.add(title, BorderLayout.NORTH);
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
  }

  private JPanel buildWideGanttChartCard() {
    RoundedPanel panel = new RoundedPanel(28, new Color(255, 255, 255, 24));
    panel.setOpaque(false);
    panel.setLayout(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(16, 18, 16, 18));
    panel.setPreferredSize(new Dimension(1000, 230));

    JLabel title = new JLabel("Graphical Gantt Chart");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 20));

    ganttChartPanel.setPreferredSize(new Dimension(1000, 165));

    panel.add(title, BorderLayout.NORTH);
    panel.add(ganttChartPanel, BorderLayout.CENTER);

    return panel;
  }

  private JPanel buildMetricsBar() {
    RoundedPanel bar = new RoundedPanel(24, new Color(255, 255, 255, 22));
    bar.setOpaque(false);
    bar.setLayout(new GridLayout(1, 5, 10, 0));
    bar.setBorder(new EmptyBorder(14, 16, 14, 16));

    bar.add(metricCard("Avg WT", avgWTMetric));
    bar.add(metricCard("Avg TAT", avgTATMetric));
    bar.add(metricCard("Avg RT", avgRTMetric));
    bar.add(metricCard("CPU Util", cpuUtilMetric));
    bar.add(metricCard("Throughput", throughputMetric));

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

  private void addProcessDialog() {
    String[] modeOptions = { "Non-Preemptive", "Preemptive" };

    String mode = (String) JOptionPane.showInputDialog(
      this,
      "Select scheduling mode for this process:",
      "Process Mode",
      JOptionPane.PLAIN_MESSAGE,
      null,
      modeOptions,
      modeOptions[0]
    );

    if (mode == null) return;

    JTextField pid = new JTextField();
    JTextField arrival = new JTextField();
    JTextField burst = new JTextField();

    JPanel panel;

    if (mode.equals("Preemptive")) {
      panel = new JPanel(new GridLayout(3, 2, 10, 10));
      panel.add(new JLabel("Process ID:"));
      panel.add(pid);
      panel.add(new JLabel("Arrival Time:"));
      panel.add(arrival);
      panel.add(new JLabel("Burst Time:"));
      panel.add(burst);
    } else {
      panel = new JPanel(new GridLayout(2, 2, 10, 10));
      panel.add(new JLabel("Process ID:"));
      panel.add(pid);
      panel.add(new JLabel("Burst Time:"));
      panel.add(burst);
    }

    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    int result = JOptionPane.showConfirmDialog(
      this,
      panel,
      "Add Process",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
      String pidText = pid.getText().trim();

      if (pidText.isEmpty()) {
        showError("Process ID cannot be empty.");
        return;
      }

      if (isDuplicatePID(pidText)) {
        showError("Duplicate PID found.");
        return;
      }

      try {
        int at = 0;

        if (mode.equals("Preemptive")) {
          at = parseNonNegativeInt(arrival.getText(), "Arrival Time");
        }

        int bt = parsePositiveInt(burst.getText(), "Burst Time");

        inputModel.addRow(new Object[] { pidText, mode, at, bt });
        refreshTableHeights();

        log(
          "[LOG] Process " + pidText +
          " added. Mode=" + mode +
          ", AT=" + at +
          ", BT=" + bt
        );

      } catch (IllegalArgumentException ex) {
        showError(ex.getMessage());
      }
    }
  }

  private boolean isDuplicatePID(String pid) {
    for (int i = 0; i < inputModel.getRowCount(); i++) {
      if (inputModel.getValueAt(i, 0).toString().equalsIgnoreCase(pid)) {
        return true;
      }
    }
    return false;
  }

  private void loadPreset() {
    inputModel.setRowCount(0);

    inputModel.addRow(new Object[] { "P1", "Preemptive", 0, 7 });
    inputModel.addRow(new Object[] { "P2", "Preemptive", 2, 4 });
    inputModel.addRow(new Object[] { "P3", "Preemptive", 4, 1 });
    inputModel.addRow(new Object[] { "P4", "Preemptive", 5, 4 });

    resultModel.setRowCount(0);
    ganttChartPanel.clear();

    refreshTableHeights();
    resetMetrics();
    log("[LOG] Preset workload loaded: P1, P2, P3, P4");
  }

  private void runScheduling() {
    if (inputModel.getRowCount() == 0) {
      showError("Add at least one process first.");
      return;
    }

    ArrayList<SProcess> processes = readProcessesFromTable();
    String algorithm = algorithmBox.getSelectedItem().toString();

    ArrayList<GanttSegment> gantt;
    String executedAlgorithm = algorithm;

    if (algorithm.equals("FCFS")) {
      gantt = runFCFS(processes);
      executedAlgorithm = "FCFS";
    } else if (algorithm.equals("SJF")) {
      if (hasPreemptiveProcess()) {
        gantt = runSRTF(processes);
        executedAlgorithm = "SJF Preemptive (SRTF)";
      } else {
        gantt = runSJFNonPreemptive(processes);
        executedAlgorithm = "SJF Non-Preemptive";
      }
    } else {
      int quantum;

      try {
        quantum = parsePositiveInt(quantumField.getText(), "Time Quantum");
      } catch (IllegalArgumentException ex) {
        showError(ex.getMessage());
        return;
      }

      gantt = runRoundRobin(processes, quantum);
      executedAlgorithm = "Round Robin";
    }

    showResults(processes, gantt, executedAlgorithm);
  }

  private boolean hasPreemptiveProcess() {
    for (int i = 0; i < inputModel.getRowCount(); i++) {
      if (inputModel.getValueAt(i, 1).toString().equalsIgnoreCase("Preemptive")) {
        return true;
      }
    }
    return false;
  }

  private ArrayList<SProcess> readProcessesFromTable() {
    ArrayList<SProcess> list = new ArrayList<>();

    for (int i = 0; i < inputModel.getRowCount(); i++) {
      String pid = inputModel.getValueAt(i, 0).toString();
      String mode = inputModel.getValueAt(i, 1).toString();
      int at = Integer.parseInt(inputModel.getValueAt(i, 2).toString());
      int bt = Integer.parseInt(inputModel.getValueAt(i, 3).toString());

      list.add(new SProcess(pid, mode, at, bt));
    }

    return list;
  }

  private ArrayList<GanttSegment> runFCFS(ArrayList<SProcess> list) {
    list.sort(Comparator.comparingInt((SProcess p) -> p.arrivalTime));

    ArrayList<GanttSegment> gantt = new ArrayList<>();
    int time = 0;

    for (SProcess p : list) {
      if (time < p.arrivalTime) {
        gantt.add(new GanttSegment("IDLE", time, p.arrivalTime));
        time = p.arrivalTime;
      }

      p.startTime = time;
      gantt.add(new GanttSegment(p.pid, time, time + p.burstTime));
      time += p.burstTime;
      p.completionTime = time;
    }

    return gantt;
  }

  private ArrayList<GanttSegment> runSJFNonPreemptive(ArrayList<SProcess> list) {
    ArrayList<GanttSegment> gantt = new ArrayList<>();
    int completed = 0;
    int time = 0;

    while (completed < list.size()) {
      SProcess selected = null;

      for (SProcess p : list) {
        if (!p.completed && p.arrivalTime <= time) {
          if (selected == null || p.burstTime < selected.burstTime) {
            selected = p;
          }
        }
      }

      if (selected == null) {
        int nextArrival = Integer.MAX_VALUE;

        for (SProcess p : list) {
          if (!p.completed) {
            nextArrival = Math.min(nextArrival, p.arrivalTime);
          }
        }

        gantt.add(new GanttSegment("IDLE", time, nextArrival));
        time = nextArrival;
        continue;
      }

      selected.startTime = time;
      gantt.add(new GanttSegment(selected.pid, time, time + selected.burstTime));
      time += selected.burstTime;
      selected.completionTime = time;
      selected.completed = true;
      completed++;
    }

    return gantt;
  }

  private ArrayList<GanttSegment> runSRTF(ArrayList<SProcess> list) {
    ArrayList<GanttSegment> gantt = new ArrayList<>();
    int completed = 0;
    int time = 0;

    while (completed < list.size()) {
      SProcess selected = null;

      for (SProcess p : list) {
        if (p.arrivalTime <= time && p.remainingTime > 0) {
          if (selected == null || p.remainingTime < selected.remainingTime) {
            selected = p;
          }
        }
      }

      if (selected == null) {
        addSegment(gantt, "IDLE", time, time + 1);
        time++;
        continue;
      }

      if (selected.startTime == -1) {
        selected.startTime = time;
      }

      selected.remainingTime--;
      addSegment(gantt, selected.pid, time, time + 1);
      time++;

      if (selected.remainingTime == 0) {
        selected.completionTime = time;
        completed++;
      }
    }

    return gantt;
  }

  private ArrayList<GanttSegment> runRoundRobin(ArrayList<SProcess> list, int quantum) {
    list.sort(Comparator.comparingInt((SProcess p) -> p.arrivalTime));

    ArrayList<GanttSegment> gantt = new ArrayList<>();
    Queue<SProcess> queue = new LinkedList<>();

    int time = 0;
    int completed = 0;
    int index = 0;

    while (completed < list.size()) {
      while (index < list.size() && list.get(index).arrivalTime <= time) {
        queue.add(list.get(index));
        index++;
      }

      if (queue.isEmpty()) {
        if (index < list.size()) {
          gantt.add(new GanttSegment("IDLE", time, list.get(index).arrivalTime));
          time = list.get(index).arrivalTime;
        }
        continue;
      }

      SProcess current = queue.poll();

      if (current.startTime == -1) {
        current.startTime = time;
      }

      int runTime = Math.min(quantum, current.remainingTime);
      gantt.add(new GanttSegment(current.pid, time, time + runTime));

      time += runTime;
      current.remainingTime -= runTime;

      while (index < list.size() && list.get(index).arrivalTime <= time) {
        queue.add(list.get(index));
        index++;
      }

      if (current.remainingTime > 0) {
        queue.add(current);
      } else {
        current.completionTime = time;
        completed++;
      }
    }

    return gantt;
  }

  private void addSegment(ArrayList<GanttSegment> gantt, String pid, int start, int end) {
    if (!gantt.isEmpty()) {
      GanttSegment last = gantt.get(gantt.size() - 1);

      if (last.pid.equals(pid) && last.end == start) {
        last.end = end;
        return;
      }
    }

    gantt.add(new GanttSegment(pid, start, end));
  }

  private void showResults(ArrayList<SProcess> processes, ArrayList<GanttSegment> gantt, String algorithm) {
    resultModel.setRowCount(0);

    double totalWT = 0;
    double totalTAT = 0;
    double totalRT = 0;
    int totalBurst = 0;

    int minArrival = Integer.MAX_VALUE;
    int maxCompletion = 0;

    for (SProcess p : processes) {
      int tat = p.completionTime - p.arrivalTime;
      int wt = tat - p.burstTime;
      int rt = p.startTime - p.arrivalTime;

      totalWT += wt;
      totalTAT += tat;
      totalRT += rt;
      totalBurst += p.burstTime;

      minArrival = Math.min(minArrival, p.arrivalTime);
      maxCompletion = Math.max(maxCompletion, p.completionTime);

      resultModel.addRow(new Object[] {
        p.pid,
        p.arrivalTime,
        p.burstTime,
        p.completionTime,
        tat,
        wt,
        rt
      });
    }

    refreshTableHeights();

    double n = processes.size();
    double totalTime = maxCompletion - minArrival;
    double cpuUtil = totalTime == 0 ? 0 : (totalBurst / totalTime) * 100.0;
    double throughput = totalTime == 0 ? 0 : n / totalTime;

    avgWTMetric.setText(String.format("%.2f", totalWT / n));
    avgTATMetric.setText(String.format("%.2f", totalTAT / n));
    avgRTMetric.setText(String.format("%.2f", totalRT / n));
    cpuUtilMetric.setText(String.format("%.2f%%", cpuUtil));
    throughputMetric.setText(String.format("%.2f", throughput));

    ganttChartPanel.setSegments(gantt);

    executionLogArea.setText("");
    log("[LOG] Algorithm selected: " + algorithm);
    log("[LOG] Scheduling completed successfully.");
    log("");
    log("DETAILED EXECUTION TRACE");
    log("----------------------------------------");

    for (GanttSegment s : gantt) {
      log("[" + s.start + " - " + s.end + "] CPU executes " + s.pid);
    }
  }

  private void refreshTableHeights() {
    adjustTableHeight(inputTable);
    adjustTableHeight(resultTable);
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

  private void resetModule() {
    inputModel.setRowCount(0);
    resultModel.setRowCount(0);

    executionLogArea.setText("[LOG] CPU Scheduling module reset.\n");
    ganttChartPanel.clear();

    refreshTableHeights();
    resetMetrics();
  }

  private void resetMetrics() {
    avgWTMetric.setText("0.00");
    avgTATMetric.setText("0.00");
    avgRTMetric.setText("0.00");
    cpuUtilMetric.setText("0.00%");
    throughputMetric.setText("0.00");
  }

  private void log(String message) {
    executionLogArea.append(message + "\n");
    executionLogArea.setCaretPosition(executionLogArea.getDocument().getLength());
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

class SProcess {
  String pid;
  String mode;
  int arrivalTime;
  int burstTime;
  int remainingTime;
  int startTime = -1;
  int completionTime = 0;
  boolean completed = false;

  SProcess(String pid, String mode, int arrivalTime, int burstTime) {
    this.pid = pid;
    this.mode = mode;
    this.arrivalTime = arrivalTime;
    this.burstTime = burstTime;
    this.remainingTime = burstTime;
  }
}

class GanttSegment {
  String pid;
  int start;
  int end;

  GanttSegment(String pid, int start, int end) {
    this.pid = pid;
    this.start = start;
    this.end = end;
  }
}

class GanttChartPanel extends JPanel {
  private java.util.List<GanttSegment> segments = new java.util.ArrayList<>();

  GanttChartPanel() {
    setOpaque(false);
  }

  void setSegments(java.util.List<GanttSegment> segments) {
    this.segments = segments;
    repaint();
  }

  void clear() {
    this.segments = new java.util.ArrayList<>();
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    int width = getWidth();
    int height = getHeight();

    GradientPaint bg = new GradientPaint(
      0, 0, new Color(7, 18, 45),
      width, height, new Color(13, 35, 78)
    );
    g2.setPaint(bg);
    g2.fillRoundRect(0, 0, width, height, 24, 24);

    g2.setColor(new Color(255, 255, 255, 35));
    g2.drawRoundRect(0, 0, width - 1, height - 1, 24, 24);

    if (segments == null || segments.isEmpty()) {
      g2.setColor(new Color(210, 225, 250));
      g2.setFont(new Font("SansSerif", Font.BOLD, 14));
      g2.drawString("Run scheduling to generate Gantt chart", 24, height / 2);
      g2.dispose();
      return;
    }

    int leftPad = 28;
    int rightPad = 28;
    int top = 68;
    int barHeight = 58;
    int usableWidth = width - leftPad - rightPad;

    int totalDuration = 0;
    for (GanttSegment s : segments) {
      totalDuration += Math.max(1, s.end - s.start);
    }

    g2.setColor(new Color(235, 242, 255));
    g2.setFont(new Font("SansSerif", Font.BOLD, 16));
    g2.drawString("CPU Timeline", 24, 30);

    g2.setColor(new Color(180, 200, 235));
    g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
    g2.drawString("Short jobs are kept visible while preserving order", 24, 50);

    g2.setColor(new Color(255, 255, 255, 25));
    g2.drawLine(leftPad, top + barHeight + 36, width - rightPad, top + barHeight + 36);

    int cursorX = leftPad;

    for (int i = 0; i < segments.size(); i++) {
      GanttSegment s = segments.get(i);
      int duration = Math.max(1, s.end - s.start);

      int proportionalWidth = (int) ((duration * 1.0 / totalDuration) * usableWidth);
      int minVisibleWidth = 68;
      int blockWidth = Math.max(minVisibleWidth, proportionalWidth);

      if (i == segments.size() - 1 && cursorX + blockWidth > width - rightPad) {
        blockWidth = Math.max(55, width - rightPad - cursorX);
      }

      if (cursorX >= width - rightPad) break;

      Color blockColor = getColorForPid(s.pid);

      g2.setColor(new Color(0, 0, 0, 60));
      g2.fillRoundRect(cursorX + 3, top + 5, blockWidth, barHeight, 18, 18);

      GradientPaint blockGradient = new GradientPaint(
        cursorX, top, blockColor.brighter(),
        cursorX, top + barHeight, blockColor.darker()
      );
      g2.setPaint(blockGradient);
      g2.fillRoundRect(cursorX, top, blockWidth, barHeight, 18, 18);

      g2.setColor(new Color(255, 255, 255, 85));
      g2.drawRoundRect(cursorX, top, blockWidth, barHeight, 18, 18);

      g2.setColor(new Color(255, 255, 255, 45));
      g2.fillRoundRect(cursorX + 4, top + 4, Math.max(0, blockWidth - 8), 14, 14, 14);

      g2.setColor(Color.WHITE);
      g2.setFont(new Font("SansSerif", Font.BOLD, 14));

      FontMetrics fm = g2.getFontMetrics();
      int textWidth = fm.stringWidth(s.pid);
      g2.drawString(s.pid, cursorX + Math.max(5, (blockWidth - textWidth) / 2), top + 35);

      g2.setColor(new Color(225, 235, 255));
      g2.setFont(new Font("SansSerif", Font.BOLD, 11));

      String interval = s.start + "–" + s.end;
      int intervalWidth = g2.getFontMetrics().stringWidth(interval);
      g2.drawString(interval, cursorX + Math.max(5, (blockWidth - intervalWidth) / 2), top + 52);

      g2.setColor(new Color(225, 235, 255));
      g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

      g2.drawLine(cursorX, top + barHeight + 8, cursorX, top + barHeight + 22);
      g2.drawString(String.valueOf(s.start), cursorX - 2, top + barHeight + 34);

      if (i == segments.size() - 1) {
        int endX = cursorX + blockWidth;
        g2.drawLine(endX, top + barHeight + 8, endX, top + barHeight + 22);
        g2.drawString(String.valueOf(s.end), endX - 8, top + barHeight + 34);
      }

      cursorX += blockWidth;
    }

    g2.dispose();
  }

  private Color getColorForPid(String pid) {
    if (pid.equalsIgnoreCase("IDLE")) {
      return new Color(100, 112, 132);
    }

    int hash = Math.abs(pid.hashCode());

    int r = 80 + hash % 120;
    int g = 110 + (hash / 3) % 100;
    int b = 150 + (hash / 7) % 80;

    return new Color(Math.min(r, 230), Math.min(g, 230), Math.min(b, 235));
  }
}