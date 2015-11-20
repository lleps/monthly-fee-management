package com.lleps.mfm.gui;

import com.alee.laf.button.WebButton;
import com.alee.laf.text.WebTextField;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.lleps.mfm.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author fedecas and leandro on 31/10/2015..
 */
public class CategoryView extends JPanel {
    private JTextField browseField;
    private JCheckBox showInactivesCheckBox;
    private JButton addClientButton;
    private JTable table;
    private JPanel pane;
    private JButton recaudationsButton;
    private JTextField monthMoneyField;
    private JButton deleteButton;

    private ActionListener showInactivesListener;
    private KeyAdapter browseStringChangeListener;
    private ActionListener addClientListener;
    private ActionListener recaudationsListener;
    private ActionListener monthMoneyChangeListener;

    private String name;
    private int previousMonthMoney;

    public CategoryView(String name) {
        super(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        this.name = name;

        browseField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (browseStringChangeListener != null) browseStringChangeListener.keyReleased(e);
            }
        });
        addClientButton.addActionListener(e -> {
            if (addClientListener != null) addClientListener.actionPerformed(e);
        });
        showInactivesCheckBox.addActionListener(e -> {
            if (showInactivesListener != null) showInactivesListener.actionPerformed(e);
        });
        recaudationsButton.addActionListener(e -> {
            if (recaudationsListener != null) recaudationsListener.actionPerformed(e);
        });
        monthMoneyField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                try {
                    previousMonthMoney = Integer.parseInt(monthMoneyField.getText());
                } catch (NumberFormatException e1) {

                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    previousMonthMoney = Integer.parseInt(monthMoneyField.getText());
                } catch (NumberFormatException e1) {
                    monthMoneyField.setText(Integer.toString(previousMonthMoney));
                }
            }
        });
        monthMoneyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (monthMoneyChangeListener != null) {
                    monthMoneyChangeListener.actionPerformed(new ActionEvent(this, 0, "mmc"));
                }
            }
        });
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    private void createUIComponents() {
        browseField = new WebTextField();
        ((WebTextField) browseField).setInputPrompt("Buscar");
        ((WebTextField) browseField).setHideInputPromptOnFocus(false);
        ((WebTextField) browseField).setLeadingComponent(new JLabel(Resources.getInstance().BROWSE_ICON));

        addClientButton = new WebButton(Resources.getInstance().ADDUSER_ICON);
        recaudationsButton = new WebButton(Resources.getInstance().GRAPHIC_ICON);
        deleteButton = new WebButton(Resources.getInstance().TRASH_ICON);

        monthMoneyField = new WebTextField(5);
        ((WebTextField) monthMoneyField).setInputPrompt("Cuota");
        ((WebTextField) monthMoneyField).setHideInputPromptOnFocus(false);
        ((WebTextField) monthMoneyField).setLeadingComponent(new JLabel(Resources.getInstance().DOLLAR_ICON));
        TooltipManager.setTooltip(monthMoneyField, "Precio de la cuota mensual", TooltipWay.up, 0);

        table = new ClientTableView();
    }

    public void setMonthMoney(int money) {
        previousMonthMoney = money;
        monthMoneyField.setText(Integer.toString(money));
    }

    public int getMonthMoney() {
        try {
            return Integer.parseInt(monthMoneyField.getText());
        } catch (NumberFormatException e) {
            return previousMonthMoney;
        }
    }

    public void setMonthMoneyChangeListener(ActionListener monthMoneyChangeListener) {
        this.monthMoneyChangeListener = monthMoneyChangeListener;
    }

    public boolean isShowInactivesMarked() {
        return showInactivesCheckBox.isSelected();
    }

    public void setShowInactivesListener(ActionListener showInactivesListener) {
        this.showInactivesListener = showInactivesListener;
    }

    public void setShowInactives(boolean show) {
        showInactivesCheckBox.setSelected(show);
    }

    public void setAddClientButtonListener(ActionListener addClientListener) {
        this.addClientListener = addClientListener;
    }

    public void setBrowseStringChangeListener(KeyAdapter browseStringChangeListener) {
        this.browseStringChangeListener = browseStringChangeListener;
    }

    public void setRecaudationsListener(ActionListener recaudationsListener) {
        this.recaudationsListener = recaudationsListener;
    }

    public String getBrowseString() {
        return browseField.getText();
    }

    public ClientTableView getTable() {
        return (ClientTableView) table;
    }
}