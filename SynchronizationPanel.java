import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class SynchronizationPanel extends JPanel {

  private final MainFrame frame;

  private final String[] resourceColumns = {
    "Resource", "Lock Type", "Current Owner", "Waiting Queue", "Status"
  };

  private final String[] eventColumns = {
    "Step", "Process", "Action", "Result"
  };

  private final DefaultTableModel resourceModel =
    new DefaultTableModel(resourceColumns, 0);

  private final DefaultTableModel eventModel =
    new DefaultTableModel(eventColumns, 0);

  private final JTable resourceTable = new JTable(resourceModel);
  private final JTable eventTable = new JTable(eventModel);

  private final JTextArea explanationArea = new JTextArea();

  private final JComboBox<String> scenarioBox = new JComboBox<>(
    new String[] {
      "Race Condition Demo",
      "Mutex Critical Section",
      "Semaphore Producer-Consumer"
    }
  );

  private final JLabel criticalMetric = new JLabel("0");
  private final JLabel waitingMetric = new JLabel("0");
  private final JLabel raceMetric = new JLabel("0");
  private final JLabel semaphoreMetric = new JLabel("0");

  private int stepCounter = 1;
  private int sharedCounter = 0;
  private int raceConditionEvents = 0;
  private int criticalSectionEntries = 0;
  private int waitingProcesses = 0;
  private int semaphoreSignals = 0;

  private String mutexOwner = "None";
  private final Queue<String> mutexQueue = new LinkedList<>();

  private int emptySlots = 3;
  private int fullSlots = 0;
  private final int bufferCapacity = 3;
  private final Queue<String> buffer = new LinkedList<>();

  public SynchronizationPanel(MainFrame frame) {
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

    JLabel title = new JLabel("Process Synchronization");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 30));

    JLabel subtitle = new JLabel(
      "Demonstrate race conditions, mutex locking, semaphores, critical sections, and producer-consumer coordination"
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
    main.add(buildExplanationPanel(), BorderLayout.EAST);

    return main;
  }

  private JPanel buildLeftControls() {
    RoundedPanel panel = new RoundedPanel(28, new Color(255, 255, 255, 24));
    panel.setOpaque(false);
    panel.setPreferredSize(new Dimension(260, 430));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 18, 20, 18));

    JLabel title = new JLabel("Sync Controls");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 21));
    title.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel scenarioLabel = label("Scenario");
    scenarioBox.setMaximumSize(new Dimension(220, 32));
    scenarioBox.setPreferredSize(new Dimension(220, 32));
    scenarioBox.addActionListener(e -> updateExplanation());

    RoundedButton runStep = sideButton("Run One Step");
    runStep.addActionListener(e -> runSelectedScenarioStep());

    RoundedButton autoDemo = sideButton("Run Full Demo");
    autoDemo.addActionListener(e -> runFullDemo());

    RoundedButton reset = sideButton("Reset Module");
    reset.addActionListener(e -> resetModule());

    panel.add(title);
    panel.add(Box.createVerticalStrut(20));
    panel.add(scenarioLabel);
    panel.add(Box.createVerticalStrut(6));
    panel.add(scenarioBox);
    panel.add(Box.createVerticalStrut(24));
    panel.add(runStep);
    panel.add(Box.createVerticalStrut(12));
    panel.add(autoDemo);
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
    button.setMaximumSize(new Dimension(220, 42));
    button.setPreferredSize(new Dimension(220, 42));
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    return button;
  }

  private JPanel buildTablesPanel() {
    JPanel wrapper = new JPanel();
    wrapper.setOpaque(false);
    wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

    JPanel resourceCard = tableCard("Synchronization Resource Table", resourceTable);
    JPanel eventCard = tableCard("Execution Event Trace Table", eventTable);

    resourceCard.setAlignmentX(Component.LEFT_ALIGNMENT);
    eventCard.setAlignmentX(Component.LEFT_ALIGNMENT);

    wrapper.add(resourceCard);
    wrapper.add(Box.createVerticalStrut(18));
    wrapper.add(eventCard);

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

  private JPanel buildExplanationPanel() {
    RoundedPanel panel = new RoundedPanel(28, new Color(255, 255, 255, 24));
    panel.setOpaque(false);
    panel.setPreferredSize(new Dimension(390, 430));
    panel.setLayout(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(18, 18, 18, 18));

    JLabel title = new JLabel("Concept Explanation");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 20));

    explanationArea.setEditable(false);
    explanationArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
    explanationArea.setBackground(new Color(8, 18, 46));
    explanationArea.setForeground(new Color(220, 235, 255));
    explanationArea.setLineWrap(true);
    explanationArea.setWrapStyleWord(true);

    JScrollPane scroll = new JScrollPane(
      explanationArea,
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
    bar.setLayout(new GridLayout(1, 4, 10, 0));
    bar.setBorder(new EmptyBorder(14, 16, 14, 16));

    bar.add(metricCard("Critical Entries", criticalMetric));
    bar.add(metricCard("Waiting", waitingMetric));
    bar.add(metricCard("Race Events", raceMetric));
    bar.add(metricCard("Semaphore Signals", semaphoreMetric));

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

  private void runSelectedScenarioStep() {
    String scenario = scenarioBox.getSelectedItem().toString();

    if (scenario.equals("Race Condition Demo")) {
      runRaceConditionStep();
    } else if (scenario.equals("Mutex Critical Section")) {
      runMutexStep();
    } else {
      runSemaphoreStep();
    }

    refreshTablesAndMetrics();
  }

  private void runFullDemo() {
    String scenario = scenarioBox.getSelectedItem().toString();

    if (scenario.equals("Race Condition Demo")) {
      for (int i = 0; i < 4; i++) runRaceConditionStep();
    } else if (scenario.equals("Mutex Critical Section")) {
      for (int i = 0; i < 6; i++) runMutexStep();
    } else {
      for (int i = 0; i < 8; i++) runSemaphoreStep();
    }

    refreshTablesAndMetrics();
  }

  private void runRaceConditionStep() {
    int oldValue = sharedCounter;

    String process = stepCounter % 2 == 0 ? "P2" : "P1";
    String action = "Read sharedCounter=" + oldValue + ", write " + (oldValue + 1);

    if (stepCounter % 3 == 0) {
      raceConditionEvents++;
      addEvent(process, action, "Race occurred: lost update possible");
    } else {
      sharedCounter = oldValue + 1;
      addEvent(process, action, "Counter updated to " + sharedCounter);
    }

    resourceModel.setRowCount(0);
    resourceModel.addRow(new Object[] {
      "sharedCounter",
      "No Lock",
      "None",
      "None",
      "Unsafe / Race Possible"
    });

    updateExplanation();
  }

  private void runMutexStep() {
    String process = "P" + ((stepCounter % 3) + 1);

    if (mutexOwner.equals("None")) {
      mutexOwner = process;
      criticalSectionEntries++;
      sharedCounter++;
      addEvent(process, "lock(mutex) + enter critical section", "Access granted. Counter=" + sharedCounter);
    } else if (mutexOwner.equals(process)) {
      addEvent(process, "unlock(mutex) + exit critical section", "Mutex released");
      mutexOwner = "None";

      if (!mutexQueue.isEmpty()) {
        String next = mutexQueue.poll();
        mutexOwner = next;
        criticalSectionEntries++;
        sharedCounter++;
        addEvent(next, "lock(mutex) from waiting queue", "Access granted. Counter=" + sharedCounter);
      }
    } else {
      if (!mutexQueue.contains(process)) {
        mutexQueue.add(process);
        waitingProcesses++;
      }
      addEvent(process, "lock(mutex)", "Blocked. Mutex owned by " + mutexOwner);
    }

    refreshResourceTableForMutex();
    updateExplanation();
  }

  private void runSemaphoreStep() {
    String process = stepCounter % 2 == 0 ? "Consumer" : "Producer";

    if (process.equals("Producer")) {
      if (emptySlots > 0) {
        emptySlots--;
        fullSlots++;
        buffer.add("Item" + stepCounter);
        semaphoreSignals++;
        addEvent("Producer", "wait(empty), produce, signal(full)", "Produced item. Buffer=" + buffer.size());
      } else {
        waitingProcesses++;
        addEvent("Producer", "wait(empty)", "Blocked. Buffer full");
      }
    } else {
      if (fullSlots > 0) {
        fullSlots--;
        emptySlots++;
        String item = buffer.poll();
        semaphoreSignals++;
        addEvent("Consumer", "wait(full), consume, signal(empty)", "Consumed " + item + ". Buffer=" + buffer.size());
      } else {
        waitingProcesses++;
        addEvent("Consumer", "wait(full)", "Blocked. Buffer empty");
      }
    }

    refreshResourceTableForSemaphore();
    updateExplanation();
  }

  private void refreshResourceTableForMutex() {
    resourceModel.setRowCount(0);

    resourceModel.addRow(new Object[] {
      "Critical Section",
      "Mutex",
      mutexOwner,
      mutexQueue.toString(),
      mutexOwner.equals("None") ? "Unlocked" : "Locked"
    });
  }

  private void refreshResourceTableForSemaphore() {
    resourceModel.setRowCount(0);

    resourceModel.addRow(new Object[] {
      "Bounded Buffer",
      "Counting Semaphore",
      "Producer/Consumer",
      "empty=" + emptySlots + ", full=" + fullSlots,
      "Buffer Items=" + buffer.size() + "/" + bufferCapacity
    });
  }

  private void addEvent(String process, String action, String result) {
    eventModel.addRow(new Object[] {
      stepCounter,
      process,
      action,
      result
    });
    stepCounter++;
  }

  private void updateExplanation() {
    String scenario = scenarioBox.getSelectedItem().toString();

    if (scenario.equals("Race Condition Demo")) {
      explanationArea.setText(
        "RACE CONDITION DEMO\n\n" +
        "This scenario shows what happens when two processes access shared data without synchronization.\n\n" +
        "Shared Resource:\n" +
        "- sharedCounter\n\n" +
        "Problem:\n" +
        "- P1 and P2 may read the same old value.\n" +
        "- Both write back an updated value.\n" +
        "- One update may be lost.\n\n" +
        "This demonstrates why critical sections need protection."
      );
    } else if (scenario.equals("Mutex Critical Section")) {
      explanationArea.setText(
        "MUTEX CRITICAL SECTION\n\n" +
        "A mutex allows only one process to enter the critical section at a time.\n\n" +
        "Rules:\n" +
        "- If mutex is free, process enters.\n" +
        "- If mutex is locked, process waits.\n" +
        "- When owner exits, the next waiting process gets access.\n\n" +
        "This prevents race conditions and protects shared data."
      );
    } else {
      explanationArea.setText(
        "SEMAPHORE PRODUCER-CONSUMER\n\n" +
        "This scenario uses counting semaphores to control a bounded buffer.\n\n" +
        "Semaphores:\n" +
        "- empty: counts available empty slots.\n" +
        "- full: counts filled slots.\n\n" +
        "Producer waits on empty and signals full.\n" +
        "Consumer waits on full and signals empty."
      );
    }
  }

  private void refreshTablesAndMetrics() {
    criticalMetric.setText(String.valueOf(criticalSectionEntries));
    waitingMetric.setText(String.valueOf(waitingProcesses));
    raceMetric.setText(String.valueOf(raceConditionEvents));
    semaphoreMetric.setText(String.valueOf(semaphoreSignals));

    adjustTableHeight(resourceTable);
    adjustTableHeight(eventTable);

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
    stepCounter = 1;
    sharedCounter = 0;
    raceConditionEvents = 0;
    criticalSectionEntries = 0;
    waitingProcesses = 0;
    semaphoreSignals = 0;

    mutexOwner = "None";
    mutexQueue.clear();

    emptySlots = bufferCapacity;
    fullSlots = 0;
    buffer.clear();

    resourceModel.setRowCount(0);
    eventModel.setRowCount(0);

    resourceModel.addRow(new Object[] {
      "sharedCounter",
      "None",
      "None",
      "None",
      "Ready"
    });

    updateExplanation();
    refreshTablesAndMetrics();
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