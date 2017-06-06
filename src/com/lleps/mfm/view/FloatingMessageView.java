package com.lleps.mfm.view;

import com.lleps.mfm.Resources;

import javax.swing.*;
import java.awt.*;

/**
 * @author Leandro B. on 04/11/2015.
 */
public class FloatingMessageView {
    private static View view;

    public static void show(String message) {
        if (view == null) view = new View();
        view.setVisible(true);
        view.setText(message);
        view.setAlwaysOnTop(true);
    }

    public static void hide() {
        if (view != null) {
            view.setVisible(false);
        }
    }

    private static class View extends JFrame {
        JLabel label;

        View() {
            setIconImage(Resources.getInstance().APP_IMAGE);
            label = new JLabel("", SwingConstants.CENTER);
            label.setForeground(Color.LIGHT_GRAY);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.BLACK);
            panel.add(label, BorderLayout.CENTER);

            add(panel, BorderLayout.CENTER);
            setUndecorated(true);
            setSize(new Dimension(300, 50));
            setLocationRelativeTo(null);
        }

        void setText(String text) {
            label.setText(text);
            update(getGraphics());
        }
    }}
