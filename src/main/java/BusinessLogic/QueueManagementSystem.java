package BusinessLogic;

import GUI.SimulationSetupFrame;

import javax.swing.*;

public class QueueManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulationSetupFrame setupFrame = new SimulationSetupFrame();
            setupFrame.setVisible(true);
        });
    }
}