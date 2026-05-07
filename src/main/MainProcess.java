package main;

import javax.swing.SwingUtilities;

import system.MainFrame;

import javax.swing.JFrame;

public class MainProcess {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MainFrame frame = new MainFrame();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}