import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class ProcessThreadPanel extends JPanel {

  private final MainFrame frame;

  private final Map<String, ProcessRecord> processMap = new HashMap<>();
  private final Map<String, ThreadRecord> threadMap = new HashMap<>();

  private final String[] pcbColumns = {
    "PID",
    "Process Name",
    "State",
    "Arrival Time",
    "Burst Time",
    "Priority",
    "Memory",
    "Threads",
  };

  private final String[] tcbColumns = {
    "TID",
    "Parent PID",
    "Thread Name",
    "State",
    "Burst Time",
    "Priority",
  };

  private final DefaultTableModel pcbModel = new DefaultTableModel(
    pcbColumns,
    0
  );
  private final DefaultTableModel tcbModel = new DefaultTableModel(
    tcbColumns,
    0
  );

  private final JTable pcbTable = new JTable(pcbModel);
  private final JTable tcbTable = new JTable(tcbModel);

  private final JTextArea eventLog = new JTextArea();

  private final JLabel processMetric = new JLabel("0");
  private final JLabel threadMetric = new JLabel("0");
  private final JLabel newMetric = new JLabel("0");
  private final JLabel readyMetric = new JLabel("0");
  private final JLabel runningMetric = new JLabel("0");
  private final JLabel waitingMetric = new JLabel("0");
  private final JLabel terminatedMetric = new JLabel("0");
  private final JLabel avgThreadMetric = new JLabel("0.00");

  public ProcessThreadPanel(MainFrame frame) {
    this.frame = frame;
    setOpaque(false);
    setLayout(new BorderLayout(20, 20));
    setBorder(new EmptyBorder(28, 34, 28, 34));

    add(buildHeader(), BorderLayout.NORTH);
    add(buildMainArea(), BorderLayout.CENTER);
    add(buildMetricsBar(), BorderLayout.SOUTH);
  }

  private JPanel buildHeader() {
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);

    JPanel titleBox = new JPanel();
    titleBox.setOpaque(false);
    titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

    JLabel title = new JLabel("Process & Thread Management");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 30));

    JLabel subtitle = new JLabel(
      "Create PCBs, TCBs, simulate lifecycle states, and inspect kernel-style control data"
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
    back.setPreferredSize(new Dimension(170, 44));
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
    main.add(buildEventLogPanel(), BorderLayout.EAST);

    return main;
  }

  private JPanel buildLeftControls() {
    RoundedPanel panel = new RoundedPanel(28, new Color(255, 255, 255, 24));
    panel.setOpaque(false);
    panel.setPreferredSize(new Dimension(230, 500));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(22, 18, 22, 18));

    JLabel title = new JLabel("Actions");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 22));
    title.setAlignmentX(Component.CENTER_ALIGNMENT);

    RoundedButton createProcess = sideButton("Create Process");
    createProcess.addActionListener(e -> createProcessDialog());

    RoundedButton createThread = sideButton("Create Thread");
    createThread.addActionListener(e -> createThreadDialog());

    RoundedButton simulateState = sideButton("Simulate State");
    simulateState.addActionListener(e -> simulateStateDialog());

    RoundedButton reset = sideButton("Reset Module");
    reset.addActionListener(e -> resetModule());

    panel.add(title);
    panel.add(Box.createVerticalStrut(24));
    panel.add(createProcess);
    panel.add(Box.createVerticalStrut(14));
    panel.add(createThread);
    panel.add(Box.createVerticalStrut(14));
    panel.add(simulateState);
    panel.add(Box.createVerticalStrut(14));
    panel.add(reset);
    panel.add(Box.createVerticalGlue());

    return panel;
  }

  private RoundedButton sideButton(String text) {
    RoundedButton button = new RoundedButton(
      text,
      new Color(88, 153, 255),
      new Color(58, 112, 218)
    );
    button.setMaximumSize(new Dimension(190, 45));
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    return button;
  }

  private JPanel buildTablesPanel() {
    JPanel wrapper = new JPanel(new GridLayout(2, 1, 0, 18));
    wrapper.setOpaque(false);

    wrapper.add(tableCard("PCB Table - Process Control Block", pcbTable));
    wrapper.add(tableCard("TCB Table - Thread Control Block", tcbTable));

    return wrapper;
  }

  private JPanel tableCard(String title, JTable table) {
    RoundedPanel card = new RoundedPanel(28, new Color(255, 255, 255, 24));
    card.setOpaque(false);
    card.setLayout(new BorderLayout(10, 10));
    card.setBorder(new EmptyBorder(18, 18, 18, 18));

    JLabel label = new JLabel(title);
    label.setForeground(Color.WHITE);
    label.setFont(new Font("SansSerif", Font.BOLD, 18));

    table.setRowHeight(28);
    table.setFont(new Font("SansSerif", Font.PLAIN, 13));
    table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(BorderFactory.createEmptyBorder());

    card.add(label, BorderLayout.NORTH);
    card.add(scroll, BorderLayout.CENTER);

    return card;
  }

  private JPanel buildEventLogPanel() {
    RoundedPanel panel = new RoundedPanel(28, new Color(255, 255, 255, 24));
    panel.setOpaque(false);
    panel.setPreferredSize(new Dimension(310, 500));
    panel.setLayout(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(18, 18, 18, 18));

    JLabel title = new JLabel("Event Trace");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 20));

    eventLog.setEditable(false);
    eventLog.setFont(new Font("Monospaced", Font.PLAIN, 13));
    eventLog.setBackground(new Color(8, 18, 46));
    eventLog.setForeground(new Color(220, 235, 255));
    eventLog.setText("[LOG] Process & Thread module loaded.\n");
    eventLog.setLineWrap(true);
    eventLog.setWrapStyleWord(true);

    JScrollPane scroll = new JScrollPane(
      eventLog,
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
    bar.setLayout(new GridLayout(1, 8, 10, 0));
    bar.setBorder(new EmptyBorder(14, 16, 14, 16));

    bar.add(metricCard("Processes", "0"));
    bar.add(metricCard("Threads", "0"));
    bar.add(metricCard("New", "0"));
    bar.add(metricCard("Ready", "0"));
    bar.add(metricCard("Running", "0"));
    bar.add(metricCard("Waiting", "0"));
    bar.add(metricCard("Terminated", "0"));
    bar.add(metricCard("Avg T/P", "0.00"));

    return bar;
  }

  private void createProcessDialog() {
    JTextField pid = new JTextField();
    JTextField name = new JTextField();
    JTextField arrival = new JTextField();
    JTextField burst = new JTextField();
    JTextField priority = new JTextField();
    JTextField memory = new JTextField();
    JTextField threads = new JTextField();

    JPanel panel = formPanel(
      new String[] {
        "Process ID:",
        "Process Name:",
        "Arrival Time:",
        "Burst Time:",
        "Priority:",
        "Memory Required (MB):",
        "Number of Threads:",
      },
      new JTextField[] { pid, name, arrival, burst, priority, memory, threads }
    );

    int result = JOptionPane.showConfirmDialog(
      this,
      panel,
      "Create Process",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
      String pidText = pid.getText().trim();
      String nameText = name.getText().trim();

      if (pidText.isEmpty() || nameText.isEmpty()) {
        showError("Process ID and Process Name cannot be empty.");
        return;
      }

      if (processMap.containsKey(pidText)) {
        showError("Duplicate PID found. Process ID must be unique.");
        return;
      }

      try {
        int arrivalValue = parseNonNegativeInt(
          arrival.getText(),
          "Arrival Time"
        );
        int burstValue = parsePositiveInt(burst.getText(), "Burst Time");
        int priorityValue = parseNonNegativeInt(priority.getText(), "Priority");
        int memoryValue = parsePositiveInt(memory.getText(), "Memory Required");
        int maxThreadsValue = parseNonNegativeInt(
          threads.getText(),
          "Number of Threads"
        );

        ProcessRecord process = new ProcessRecord(
          pidText,
          nameText,
          "New",
          arrivalValue,
          burstValue,
          priorityValue,
          memoryValue,
          maxThreadsValue
        );

        processMap.put(pidText, process);

        pcbModel.addRow(
          new Object[] {
            process.pid,
            process.name,
            process.state,
            process.arrivalTime,
            process.burstTime,
            process.priority,
            process.memory + " MB",
            process.currentThreads + "/" + process.maxThreads,
          }
        );

        log("[LOG] Process " + pidText + " created. State = New");
        updateMetrics();
      } catch (IllegalArgumentException ex) {
        showError(ex.getMessage());
      }
    }
  }

  private void createThreadDialog() {
    JTextField parentPid = new JTextField();
    JTextField tid = new JTextField();
    JTextField name = new JTextField();
    JTextField burst = new JTextField();
    JTextField priority = new JTextField();

    JPanel panel = formPanel(
      new String[] {
        "Parent Process ID:",
        "Thread ID:",
        "Thread Name:",
        "Thread Burst Time:",
        "Thread Priority:",
      },
      new JTextField[] { parentPid, tid, name, burst, priority }
    );

    int result = JOptionPane.showConfirmDialog(
      this,
      panel,
      "Create Thread",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
      String parentText = parentPid.getText().trim();
      String tidText = tid.getText().trim();
      String nameText = name.getText().trim();

      if (parentText.isEmpty() || tidText.isEmpty() || nameText.isEmpty()) {
        showError("Parent PID, Thread ID, and Thread Name cannot be empty.");
        return;
      }

      if (!processMap.containsKey(parentText)) {
        showError("Parent process does not exist. Create the process first.");
        return;
      }

      if (threadMap.containsKey(tidText)) {
        showError("Duplicate TID found. Thread ID must be unique.");
        return;
      }

      ProcessRecord parentProcess = processMap.get(parentText);

      if (parentProcess.currentThreads >= parentProcess.maxThreads) {
        showError(
          "Thread limit reached for process " +
            parentText +
            ". Allowed: " +
            parentProcess.maxThreads
        );
        return;
      }

      try {
        int burstValue = parsePositiveInt(burst.getText(), "Thread Burst Time");
        int priorityValue = parseNonNegativeInt(
          priority.getText(),
          "Thread Priority"
        );

        ThreadRecord thread = new ThreadRecord(
          tidText,
          parentText,
          nameText,
          "New",
          burstValue,
          priorityValue
        );

        threadMap.put(tidText, thread);
        parentProcess.currentThreads++;

        tcbModel.addRow(
          new Object[] {
            thread.tid,
            thread.parentPid,
            thread.name,
            thread.state,
            thread.burstTime,
            thread.priority,
          }
        );

        refreshPCBTable();

        log("[LOG] Thread " + tidText + " created under Process " + parentText);
        updateMetrics();
      } catch (IllegalArgumentException ex) {
        showError(ex.getMessage());
      }
    }
  }

  private JPanel formPanel(String[] labels, JTextField[] fields) {
    JPanel panel = new JPanel(new GridLayout(labels.length, 2, 10, 10));
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    for (int i = 0; i < labels.length; i++) {
      panel.add(new JLabel(labels[i]));
      panel.add(fields[i]);
    }

    return panel;
  }

  private void simulateStateDialog() {
    String[] targetTypes = { "Process", "Thread" };

    String type = (String) JOptionPane.showInputDialog(
      this,
      "Choose target type:",
      "State Transition",
      JOptionPane.PLAIN_MESSAGE,
      null,
      targetTypes,
      targetTypes[0]
    );

    if (type == null) return;

    String id = JOptionPane.showInputDialog(this, "Enter " + type + " ID:");
    if (id == null || id.trim().isEmpty()) return;

    id = id.trim();

    String currentState;

    if (type.equals("Process")) {
      ProcessRecord process = processMap.get(id);

      if (process == null) {
        showError("Process ID not found.");
        return;
      }

      currentState = process.state;
    } else {
      ThreadRecord thread = threadMap.get(id);

      if (thread == null) {
        showError("Thread ID not found.");
        return;
      }

      currentState = thread.state;
    }

    if (currentState.equalsIgnoreCase("Terminated")) {
      showError(
        type + " is already terminated. No further transition is allowed."
      );
      return;
    }

    String[] transitions = getValidTransitions(currentState);

    if (transitions.length == 0) {
      showError(
        "No valid transition available from current state: " + currentState
      );
      return;
    }

    String transition = (String) JOptionPane.showInputDialog(
      this,
      "Current State: " + currentState + "\nChoose valid next transition:",
      "State Transition",
      JOptionPane.PLAIN_MESSAGE,
      null,
      transitions,
      transitions[0]
    );

    if (transition == null) return;

    String toState = transition.substring(transition.indexOf("→") + 1).trim();

    if (type.equals("Process")) {
      processMap.get(id).state = toState;
      refreshPCBTable();
    } else {
      threadMap.get(id).state = toState;
      refreshTCBTable();
    }

    log("[LOG] " + type + " " + id + " transitioned: " + transition);
    updateMetrics();
  }

  private String[] getValidTransitions(String currentState) {
    if (currentState.equalsIgnoreCase("New")) {
      return new String[] { "New → Ready" };
    } else if (currentState.equalsIgnoreCase("Ready")) {
      return new String[] { "Ready → Running" };
    } else if (currentState.equalsIgnoreCase("Running")) {
      return new String[] { "Running → Waiting", "Running → Terminated" };
    } else if (currentState.equalsIgnoreCase("Waiting")) {
      return new String[] { "Waiting → Ready" };
    }
    return new String[] {};
  }

  private void refreshPCBTable() {
    pcbModel.setRowCount(0);

    for (ProcessRecord process : processMap.values()) {
      pcbModel.addRow(
        new Object[] {
          process.pid,
          process.name,
          process.state,
          process.arrivalTime,
          process.burstTime,
          process.priority,
          process.memory + " MB",
          process.currentThreads + "/" + process.maxThreads,
        }
      );
    }
  }

  private void refreshTCBTable() {
    tcbModel.setRowCount(0);

    for (ThreadRecord thread : threadMap.values()) {
      tcbModel.addRow(
        new Object[] {
          thread.tid,
          thread.parentPid,
          thread.name,
          thread.state,
          thread.burstTime,
          thread.priority,
        }
      );
    }
  }

  private int parsePositiveInt(String value, String fieldName) {
    try {
      int parsed = Integer.parseInt(value.trim());
      if (parsed <= 0) {
        throw new IllegalArgumentException(
          fieldName + " must be greater than 0."
        );
      }
      return parsed;
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(
        fieldName + " must be a valid number."
      );
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
      throw new IllegalArgumentException(
        fieldName + " must be a valid number."
      );
    }
  }

  private void resetModule() {
    processMap.clear();
    threadMap.clear();
    pcbModel.setRowCount(0);
    tcbModel.setRowCount(0);
    eventLog.setText("[LOG] Module reset.\n");
    updateMetrics();
  }

  private void log(String message) {
    eventLog.append(message + "\n");
    eventLog.setCaretPosition(eventLog.getDocument().getLength());
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(
      this,
      message,
      "Input Error",
      JOptionPane.ERROR_MESSAGE
    );
  }

  private void updateMetrics() {
    int processCount = processMap.size();
    int threadCount = threadMap.size();

    int newCount = 0;
    int ready = 0;
    int running = 0;
    int waiting = 0;
    int terminated = 0;

    for (ProcessRecord process : processMap.values()) {
      String state = process.state;

      if (state.equalsIgnoreCase("New")) newCount++;
      else if (state.equalsIgnoreCase("Ready")) ready++;
      else if (state.equalsIgnoreCase("Running")) running++;
      else if (state.equalsIgnoreCase("Waiting")) waiting++;
      else if (state.equalsIgnoreCase("Terminated")) terminated++;
    }

    double avgThreads =
      processCount == 0 ? 0.0 : (double) threadCount / processCount;

    processMetric.setText(String.valueOf(processCount));
    threadMetric.setText(String.valueOf(threadCount));
    newMetric.setText(String.valueOf(newCount));
    readyMetric.setText(String.valueOf(ready));
    runningMetric.setText(String.valueOf(running));
    waitingMetric.setText(String.valueOf(waiting));
    terminatedMetric.setText(String.valueOf(terminated));
    avgThreadMetric.setText(String.format("%.2f", avgThreads));
  }

  private JPanel metricCard(String title, String value) {
    JPanel card = new JPanel();
    card.setOpaque(false);
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

    JLabel valueLabel;

    if (title.equals("Processes")) valueLabel = processMetric;
    else if (title.equals("Threads")) valueLabel = threadMetric;
    else if (title.equals("New")) valueLabel = newMetric;
    else if (title.equals("Ready")) valueLabel = readyMetric;
    else if (title.equals("Running")) valueLabel = runningMetric;
    else if (title.equals("Waiting")) valueLabel = waitingMetric;
    else if (title.equals("Terminated")) valueLabel = terminatedMetric;
    else valueLabel = avgThreadMetric;

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

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

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

class ProcessRecord {

  String pid;
  String name;
  String state;
  int arrivalTime;
  int burstTime;
  int priority;
  int memory;
  int maxThreads;
  int currentThreads;

  ProcessRecord(
    String pid,
    String name,
    String state,
    int arrivalTime,
    int burstTime,
    int priority,
    int memory,
    int maxThreads
  ) {
    this.pid = pid;
    this.name = name;
    this.state = state;
    this.arrivalTime = arrivalTime;
    this.burstTime = burstTime;
    this.priority = priority;
    this.memory = memory;
    this.maxThreads = maxThreads;
    this.currentThreads = 0;
  }
}

class ThreadRecord {

  String tid;
  String parentPid;
  String name;
  String state;
  int burstTime;
  int priority;

  ThreadRecord(
    String tid,
    String parentPid,
    String name,
    String state,
    int burstTime,
    int priority
  ) {
    this.tid = tid;
    this.parentPid = parentPid;
    this.name = name;
    this.state = state;
    this.burstTime = burstTime;
    this.priority = priority;
  }
}
