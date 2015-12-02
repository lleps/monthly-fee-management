package com.lleps.mfm.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OkCancelDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel editablePane;

    private ActionListener okListener;
    private ActionListener cancelListener;

    private Timer updateOkTimer;

    public OkCancelDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(this::onOK);
        buttonCancel.addActionListener(this::onCancel);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel(null);
            }

            public void windowActivated(WindowEvent e) {
                updateOkTimer.start();
            }

            public void windowDeactivated(WindowEvent e) {
                updateOkTimer.stop();
            }
        });

        contentPane.registerKeyboardAction(this::onCancel, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        updateOkTimer = new Timer(500, e -> buttonOK.setEnabled(!shouldDisableOK()));
    }

    protected void setPanel(JPanel panel) {
        editablePane.add(panel, BorderLayout.CENTER);
    }

    public void setOkListener(ActionListener okListener) {
        this.okListener = okListener;
    }

    public void setCancelListener(ActionListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    private void onOK(ActionEvent e) {
        if (shouldDisableOK()) {
            buttonOK.setEnabled(false);
            return;
        }
        if (okListener != null) {
            okListener.actionPerformed(e);
        }
    }

    private void onCancel(ActionEvent e) {
        if (cancelListener != null) {
            cancelListener.actionPerformed(e);
        }
    }

    protected boolean shouldDisableOK() {
        return false;
    }
}