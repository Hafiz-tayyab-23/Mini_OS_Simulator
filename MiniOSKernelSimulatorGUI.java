import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class MiniOSKernelSimulatorGUI {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ignored) {}
      new MainFrame().setVisible(true);
    });
  }
}

class MainFrame extends JFrame {

  private final CardLayout cardLayout = new CardLayout();
  private final JPanel container = new JPanel(cardLayout);

  MainFrame() {
    setTitle("Mini Operating System Kernel Simulator");
    setSize(1200, 760);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(1000, 650));

    container.add(new WelcomePanel(this), "WELCOME");
    container.add(new MainMenuPanel(this), "MENU");
    container.add(new ProcessThreadPanel(this), "PROCESS_THREAD");
    container.add(new CPUSchedulingPanel(this), "CPU_SCHEDULING");
    container.add(new SynchronizationPanel(this), "SYNCHRONIZATION");
    container.add(new MemoryManagementPanel(this), "MEMORY");

    setContentPane(container);
    showWelcome();
  }

  void showWelcome() {
    cardLayout.show(container, "WELCOME");
  }

  void showMenu() {
    cardLayout.show(container, "MENU");
  }

  void showProcessThreadModule() {
    cardLayout.show(container, "PROCESS_THREAD");
  }

  void showCPUSchedulingModule() {
    cardLayout.show(container, "CPU_SCHEDULING");
  }

  void showSynchronizationModule() {
    cardLayout.show(container, "SYNCHRONIZATION");
  }

  void showMemoryManagementModule() {
    cardLayout.show(container, "MEMORY");
  }
}

class WelcomePanel extends JPanel {

  private final MainFrame frame;

  WelcomePanel(MainFrame frame) {
    this.frame = frame;
    setLayout(new BorderLayout());
    setOpaque(false);

    JPanel center = new JPanel();
    center.setOpaque(false);
    center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
    center.setBorder(new EmptyBorder(120, 70, 120, 70));

    JLabel badge = createLabel(
      "OPERATING SYSTEM SEMESTER PROJECT",
      18,
      new Color(214, 228, 255, 210)
    );
    badge.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel title1 = createLabel("Mini Operating System", 42, Color.WHITE);
    title1.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel title2 = createLabel(
      "Kernel Simulator",
      42,
      new Color(197, 220, 255)
    );
    title2.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel subtitle = createLabel(
      "A polished front-end shell for process, scheduling, synchronization, and paging simulation",
      18,
      new Color(220, 228, 242)
    );
    subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel membersTitle = createLabel(
      "Project Members",
      20,
      new Color(220, 230, 255)
    );
    membersTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel member1 = createLabel(
      "Major Mursaleen Ahmad",
      17,
      new Color(240, 245, 255)
    );
    member1.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel member2 = createLabel(
      "Hafiz Muhammad Tayyab Zia",
      17,
      new Color(240, 245, 255)
    );
    member2.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel member3 = createLabel(
      "Faique Mustafa",
      17,
      new Color(240, 245, 255)
    );
    member3.setAlignmentX(Component.CENTER_ALIGNMENT);

    RoundedButton enterButton = new RoundedButton(
      "Enter OS",
      new Color(82, 141, 255),
      new Color(52, 102, 210)
    );
    enterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    enterButton.setPreferredSize(new Dimension(220, 54));
    enterButton.setMaximumSize(new Dimension(220, 54));
    enterButton.addActionListener(e -> frame.showMenu());

    center.add(Box.createVerticalGlue());
    center.add(badge);
    center.add(Box.createVerticalStrut(22));
    center.add(title1);
    center.add(Box.createVerticalStrut(8));
    center.add(title2);
    center.add(Box.createVerticalStrut(18));
    center.add(subtitle);
    center.add(Box.createVerticalStrut(28));

    center.add(membersTitle);
    center.add(Box.createVerticalStrut(10));
    center.add(member1);
    center.add(Box.createVerticalStrut(5));
    center.add(member2);
    center.add(Box.createVerticalStrut(5));
    center.add(member3);

    center.add(Box.createVerticalStrut(38));
    center.add(enterButton);
    center.add(Box.createVerticalGlue());

    add(center, BorderLayout.CENTER);
  }

  private JPanel infoItem(String title, String text) {
    JPanel panel = new JPanel();
    panel.setOpaque(false);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JLabel t = createLabel(title, 18, Color.WHITE);
    JLabel d = new JLabel(
      "<html><div style='width:220px'>" + text + "</div></html>"
    );
    d.setForeground(new Color(225, 232, 245));
    d.setFont(new Font("SansSerif", Font.PLAIN, 14));

    panel.add(t);
    panel.add(Box.createVerticalStrut(8));
    panel.add(d);
    return panel;
  }

  private JLabel createLabel(String text, int size, Color color) {
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    label.setForeground(color);
    label.setFont(new Font("SansSerif", Font.BOLD, size));
    return label;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
  }

  @Override
  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    GradientPaint gp = new GradientPaint(
      0,
      0,
      new Color(8, 18, 46),
      getWidth(),
      getHeight(),
      new Color(24, 68, 145)
    );
    g2.setPaint(gp);
    g2.fillRect(0, 0, getWidth(), getHeight());

    g2.setColor(new Color(255, 255, 255, 18));
    g2.fillOval(-80, -40, 360, 360);
    g2.fillOval(getWidth() - 300, 60, 260, 260);
    g2.fillOval(getWidth() / 2 - 150, getHeight() - 180, 380, 220);

    g2.dispose();
    super.paint(g);
  }
}

class MainMenuPanel extends JPanel {

  private final MainFrame frame;

  MainMenuPanel(MainFrame frame) {
    this.frame = frame;
    setOpaque(false);
    setLayout(new BorderLayout(20, 20));
    setBorder(new EmptyBorder(30, 35, 30, 35));

    add(buildHeader(), BorderLayout.NORTH);
    add(buildGrid(), BorderLayout.CENTER);
    add(buildFooter(), BorderLayout.SOUTH);
  }

  private JPanel buildHeader() {
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);

    JPanel titleWrap = new JPanel();
    titleWrap.setOpaque(false);
    titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));

    JLabel title = new JLabel("Main Control Center");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 30));

    JLabel subtitle = new JLabel(
      "Select a subsystem to launch simulation workflows"
    );
    subtitle.setForeground(new Color(214, 225, 245));
    subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));

    titleWrap.add(title);
    titleWrap.add(Box.createVerticalStrut(8));
    titleWrap.add(subtitle);

    RoundedButton back = new RoundedButton(
      "Back",
      new Color(36, 58, 99),
      new Color(25, 40, 72)
    );
    back.setPreferredSize(new Dimension(130, 44));
    back.addActionListener(e -> frame.showWelcome());

    header.add(titleWrap, BorderLayout.WEST);
    header.add(back, BorderLayout.EAST);
    return header;
  }

  private JPanel buildGrid() {
    JPanel grid = new JPanel(new GridLayout(2, 2, 24, 24));
    grid.setOpaque(false);

    grid.add(
      moduleCard(
        "Process & Thread Management",
        "Create processes, define threads, manage lifecycle states, and inspect process control data.",
        "Process creation • Thread creation • PCB/TCB view • State transitions",
        () -> frame.showProcessThreadModule()
      )
    );

    grid.add(
      moduleCard(
        "CPU Scheduling",
        "Run FCFS, SJF/SRTF, and Round Robin with user-driven parameters and visual execution traces.",
        "Arrival time • Burst time • Preemptive mode • Gantt chart",
        () -> frame.showCPUSchedulingModule()
      )
    );

    grid.add(
      moduleCard(
        "Process Synchronization",
        "Demonstrate mutex and semaphore behavior using critical-section and producer-consumer scenarios.",
        "Race condition • Locking • Shared resources • Event trace",
        () -> frame.showSynchronizationModule()
      )
    );

    grid.add(
      moduleCard(
        "Memory Management",
        "Simulate paging, address translation, and page replacement with frame-by-frame analysis.",
        "Reference strings • Logical addresses • Page faults • Frame table",
        () -> frame.showMemoryManagementModule()
      )
    );

    return grid;
  }

  private JPanel moduleCard(
    String title,
    String description,
    String highlights,
    Runnable action
  ) {
    RoundedPanel card = new RoundedPanel(30, new Color(255, 255, 255, 28));
    card.setOpaque(false);
    card.setLayout(new BorderLayout(10, 10));
    card.setBorder(new EmptyBorder(26, 26, 26, 26));

    JPanel top = new JPanel();
    top.setOpaque(false);
    top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

    JLabel titleLabel = new JLabel(title);
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

    JLabel descLabel = new JLabel(
      "<html><div style='width:420px'>" + description + "</div></html>"
    );
    descLabel.setForeground(new Color(223, 230, 244));
    descLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

    JPanel chip = new RoundedPanel(20, new Color(82, 141, 255, 80));
    chip.setOpaque(false);
    chip.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 8));
    JLabel chipLabel = new JLabel(highlights);
    chipLabel.setForeground(new Color(238, 244, 255));
    chipLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
    chip.add(chipLabel);

    RoundedButton open = new RoundedButton(
      "Open Module",
      new Color(88, 153, 255),
      new Color(58, 112, 218)
    );
    open.setPreferredSize(new Dimension(170, 42));
    open.addActionListener(e -> action.run());

    top.add(titleLabel);
    top.add(Box.createVerticalStrut(12));
    top.add(descLabel);
    top.add(Box.createVerticalStrut(18));
    top.add(chip);

    card.add(top, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    bottom.setOpaque(false);
    bottom.add(open);
    card.add(bottom, BorderLayout.SOUTH);

    return card;
  }

  private JPanel buildFooter() {
    RoundedPanel footer = new RoundedPanel(26, new Color(255, 255, 255, 20));
    footer.setOpaque(false);
    footer.setBorder(new EmptyBorder(18, 22, 18, 22));
    footer.setLayout(new BorderLayout());

    JLabel left = new JLabel(
      "Refined modules: Process & Thread Management, CPU Scheduling, Process Synchronization, Memory Management"
    );
    left.setForeground(new Color(225, 233, 246));
    left.setFont(new Font("SansSerif", Font.PLAIN, 14));

    JLabel right = new JLabel("Java GUI Starter • Premium dark theme");
    right.setForeground(new Color(200, 217, 250));
    right.setFont(new Font("SansSerif", Font.BOLD, 14));

    footer.add(left, BorderLayout.WEST);
    footer.add(right, BorderLayout.EAST);
    return footer;
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

    g2.setColor(new Color(255, 255, 255, 16));
    g2.fillOval(-120, 80, 300, 300);
    g2.fillOval(getWidth() - 280, getHeight() - 220, 250, 250);
    g2.dispose();
  }
}

class RoundedPanel extends JPanel {

  private final int radius;
  private final Color backgroundColor;

  RoundedPanel(int radius, Color backgroundColor) {
    this.radius = radius;
    this.backgroundColor = backgroundColor;
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );
    g2.setColor(backgroundColor);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
    g2.setColor(new Color(255, 255, 255, 28));
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
    g2.dispose();
    super.paintComponent(g);
  }
}

class RoundedButton extends JButton {

  private final Color startColor;
  private final Color endColor;

  RoundedButton(String text, Color startColor, Color endColor) {
    super(text);
    this.startColor = startColor;
    this.endColor = endColor;
    setForeground(Color.WHITE);
    setFont(new Font("SansSerif", Font.BOLD, 16));
    setContentAreaFilled(false);
    setFocusPainted(false);
    setBorderPainted(false);
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    setBorder(new EmptyBorder(10, 20, 10, 20));
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    int w = getWidth();
    int h = getHeight();
    GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
    g2.setPaint(gp);
    g2.fillRoundRect(0, 0, w, h, 22, 22);

    if (getModel().isRollover()) {
      g2.setColor(new Color(255, 255, 255, 35));
      g2.fillRoundRect(0, 0, w, h, 22, 22);
    }

    super.paintComponent(g);
    g2.dispose();
  }
}
