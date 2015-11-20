package com.lleps.mfm.gui;

import com.lleps.mfm.Resources;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * @author Leandro B. on 01/11/2015.
 */
public class CategoryPaymentsView extends JFrame {
    private JPanel panel0;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel mainPanel;
    private JButton previousButton;
    private JButton nextButton;

    private ActionListener nextListener;
    private ActionListener previousListener;

    private JPanel panels[] = {panel0, panel1, panel2, panel3};
    private MonthPaymentsView[] paymentViews = new MonthPaymentsView[panels.length];

    private int getPanelCount() {
        return panels.length;
    }

    private JPanel getPanelByIndex(int index) {
        return panels[index];
    }

    public CategoryPaymentsView() {
        setContentPane(mainPanel);
        setIconImage(Resources.getInstance().APP_IMAGE);
        for (int i = 0; i < getPanelCount(); i++) {
            paymentViews[i] = new MonthPaymentsView();
            getPanelByIndex(i).add(paymentViews[i]);
        }

        nextButton.addActionListener(e -> {
            if (nextListener != null) {
                nextListener.actionPerformed(e);
            }
        });

        previousButton.addActionListener(e -> {
            if (previousListener != null) {
                previousListener.actionPerformed(e);
            }
        });
    }

    public int getMonthCount() {
        return getPanelCount();
    }

    public MonthPaymentsView getMonthPaymentView(int index) {
        return paymentViews[index];
    }

    public void setNextListener(ActionListener nextListener) {
        this.nextListener = nextListener;
    }

    public void setPreviousListener(ActionListener previousListener) {
        this.previousListener = previousListener;
    }
}
