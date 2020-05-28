package com.lleps.mfm.view;

import com.lleps.mfm.Resources;
import com.lleps.mfm.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Leandro B. on 01/11/2015.
 */
public class CategoryPaymentsView extends JFrame {
    private JPanel p0;
    private JPanel p1;
    private JPanel p4;
    private JPanel p5;
    private JPanel mainPanel;
    private JButton theNextButton;
    private JButton thePrevButton;
    private JPanel p8;
    private JPanel p9;
    private JPanel p6;
    private JPanel p7;
    private JPanel p2;
    private JPanel p3;
    private JLabel totalMoney;

    private ActionListener nextListener;
    private ActionListener previousListener;

    private JPanel panels[] = {p7, p6, p5, p4, p3, p2, p1, p0};
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

        thePrevButton.addActionListener(e -> {
            if (previousListener != null) {
                previousListener.actionPerformed(e);
            }
        });

        theNextButton.addActionListener(e -> {
            if (nextListener != null) {
                nextListener.actionPerformed(e);
            }
        });

        ActionListener taskPerformer = evt -> {
            //...Perform a task...
            int totalMoneyCounter = 0;
            for (int i = 0; i < paymentViews.length; i++) {
                totalMoneyCounter += paymentViews[i].getMoney();
            }
            totalMoney.setText(Utils.priceToString(totalMoneyCounter));
        };
        Timer timer = new Timer(500 ,taskPerformer);
        timer.setRepeats(true);
        timer.start();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                timer.stop();
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
