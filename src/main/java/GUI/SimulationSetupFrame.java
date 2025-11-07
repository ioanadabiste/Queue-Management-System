package GUI;
import BusinessLogic.ConcreteStrategyTime;
import BusinessLogic.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SimulationSetupFrame extends JFrame {
    private JTextField clientsField, queuesField, simTimeField;
    private JTextField minArrivalField, maxArrivalField;
    private JTextField minServiceField, maxServiceField;
    private JComboBox<String> strategyCombo;

    public SimulationSetupFrame() {
        setTitle("Queue Simulation Setup");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));

        // Adăugare componente
        panel.add(new JLabel("Number of Clients:"));
        clientsField = new JTextField("20");
        panel.add(clientsField);

        panel.add(new JLabel("Number of Queues:"));
        queuesField = new JTextField("3");
        panel.add(queuesField);

        panel.add(new JLabel("Simulation Time:"));
        simTimeField = new JTextField("60");
        panel.add(simTimeField);

        panel.add(new JLabel("Min Arrival Time:"));
        minArrivalField = new JTextField("2");
        panel.add(minArrivalField);

        panel.add(new JLabel("Max Arrival Time:"));
        maxArrivalField = new JTextField("30");
        panel.add(maxArrivalField);

        panel.add(new JLabel("Min Service Time:"));
        minServiceField = new JTextField("2");
        panel.add(minServiceField);

        panel.add(new JLabel("Max Service Time:"));
        maxServiceField = new JTextField("4");
        panel.add(maxServiceField);

        panel.add(new JLabel("Strategy:"));
        strategyCombo = new JComboBox<>(new String[]{"Shortest Queue", "Shortest Time"});
        panel.add(strategyCombo);

        JButton startButton = new JButton("Start Simulation");
        startButton.addActionListener(this::startSimulation);
        panel.add(startButton);

        add(panel, BorderLayout.CENTER);
    }

    private void startSimulation(ActionEvent e) {
        try {
            int clients = Integer.parseInt(clientsField.getText());
            int queues = Integer.parseInt(queuesField.getText());
            int simTime = Integer.parseInt(simTimeField.getText());
            int minArrival = Integer.parseInt(minArrivalField.getText());
            int maxArrival = Integer.parseInt(maxArrivalField.getText());
            int minService = Integer.parseInt(minServiceField.getText());
            int maxService = Integer.parseInt(maxServiceField.getText());
            ConcreteStrategyTime.SelectionPolicy policy = strategyCombo.getSelectedIndex() == 0 ?
                    ConcreteStrategyTime.SelectionPolicy.SHORTEST_QUEUE : ConcreteStrategyTime.SelectionPolicy.SHORTEST_TIME;

            SimulationManager manager = new SimulationManager(
                    clients, queues, simTime,
                    minArrival, maxArrival,
                    minService, maxService,
                    policy
            );

            SimulationFrame simFrame = new SimulationFrame(manager);
            simFrame.setVisible(true);
            this.dispose();

            new Thread(manager).start();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input values!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}