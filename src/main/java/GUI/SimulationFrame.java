package GUI;

import BusinessLogic.SimulationManager;
import Model.Server;
import Model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationFrame extends JFrame {
    private JTextArea logArea;
    private JPanel queuesPanel;
    private JLabel resultsLabel;
    private SimulationManager manager;

    public SimulationFrame(SimulationManager manager) {
        this.manager = manager;
        manager.setSimulationFrame(this);

        setTitle("Queue Simulation");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Results panel
        JPanel resultsPanel = new JPanel();
        resultsLabel = new JLabel("Simulation in progress...");
        resultsPanel.add(resultsLabel);
        mainPanel.add(resultsPanel, BorderLayout.NORTH);

        // Queues panel
        queuesPanel = new JPanel();
        queuesPanel.setLayout(new GridLayout(0, 1, 5, 5));
        updateQueuesDisplay();
        JScrollPane queuesScroll = new JScrollPane(queuesPanel);
        queuesScroll.setPreferredSize(new Dimension(400, 600));
        mainPanel.add(queuesScroll, BorderLayout.CENTER);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(400, 600));
        mainPanel.add(logScroll, BorderLayout.EAST);

        add(mainPanel);

        // Timer for UI updates
        Timer timer = new Timer(1000, e -> updateDisplay());
        timer.start();
    }

    public void simulationComplete() {
        SwingUtilities.invokeLater(() -> {
            resultsLabel.setText("Simulation complete! Average waiting time: " +
                    manager.calculateAverageWaitingTime());
        });
    }

    private void updateDisplay() {
        updateQueuesDisplay();
        logArea.setText(manager.getLog());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void updateQueuesDisplay() {
        queuesPanel.removeAll();
        List<Server> servers = manager.getScheduler().getServers();

        for (Server server : servers) {
            JPanel serverPanel = new JPanel(new BorderLayout());
            serverPanel.setBorder(BorderFactory.createTitledBorder("Queue " + server.getId()));
            serverPanel.setPreferredSize(new Dimension(350, 150));

            JTextArea tasksArea = new JTextArea();
            tasksArea.setEditable(false);
            StringBuilder sb = new StringBuilder();

            if (server.getQueueSize() == 0) {
                sb.append("Closed");
            }else {
                sb.append("Active tasks: ").append(server.getQueueSize()).append("\n");
                sb.append("Total waiting time: ").append(server.getWaitingPeriod()).append("\n\n");

                for (Task task : server.getTasks()) {
                    sb.append(task.toString()).append("\n");
                }
            }

            tasksArea.setText(sb.toString());
            serverPanel.add(new JScrollPane(tasksArea), BorderLayout.CENTER);
            queuesPanel.add(serverPanel);
        }

        queuesPanel.revalidate();
        queuesPanel.repaint();
    }
}