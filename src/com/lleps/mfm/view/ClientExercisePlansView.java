package com.lleps.mfm.view;

import com.alee.laf.button.WebButton;
import com.lleps.mfm.Resources;
import com.lleps.mfm.Storage;
import com.lleps.mfm.Utils;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.ExercisePlan;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientExercisePlansView extends JDialog {
    private JPanel contentPane;
    private JButton plan1;
    private JButton plan2;
    private JButton addPlanButton;
    private JButton plan3;
    private JButton plan4;
    private JButton plan5;
    private JButton[] plans = new JButton[] { plan1, plan2, plan3, plan4, plan5 };
    private Category category;
    private Client client;

    public ClientExercisePlansView(Category category, Client client) {
        this.category = category;
        this.client = client;

        // For files serialised with older versions
        if (client.getExercisePlans() == null) client.setExercisePlans(new ArrayList<>());

        setIconImage(Resources.getInstance().APP_IMAGE);
        setContentPane(contentPane);
        setModal(true);
        setSize(300, 250);
        setTitle("Planes de " + client.getFirstName() + " " + client.getLastName());

        // Click handler for plans
        for (int i = 0; i < plans.length; i++) {
            int planIndex = i;
            plans[i].addActionListener(e -> {
                if (planIndex < client.getExercisePlans().size()) {
                    Utils.doUsingNativeLAF(() -> {
                        ExercisePlanView view = new ExercisePlanView(category, client, client.getExercisePlans().get(planIndex));
                        view.pack();
                        view.setLocationRelativeTo(this);
                        view.setVisible(true);
                    });
                    updatePlanButtonsVisibility();
                }
            });
        }

        addPlanButton.addActionListener(e -> {
            String planName = (String) JOptionPane.showInputDialog(this,
                    "Escribe el nombre del plan",
                    "Crear nuevo plan",
                    JOptionPane.PLAIN_MESSAGE,
                    Resources.getInstance().PLUS_ICON, null,
                    "Nuevo plan");

            if (planName != null) {
                String[][] exercises = new String[25][4];
                for (int i = 0; i < exercises.length; i++) {
                    for (int j = 0; j < exercises[i].length; j++) {
                        exercises[i][j] = "";
                    }
                }

                List<ExercisePlan> plans = new ArrayList<>(client.getExercisePlans());
                plans.add(new ExercisePlan(planName, LocalDate.now(), exercises));
                client.setExercisePlans(plans);
                try {
                    Storage.getInstance().saveCategoryClients(category);
                } catch (IOException e1) {
                    Utils.reportException(e1, "error saving category");
                }
                updatePlanButtonsVisibility();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        updatePlanButtonsVisibility();
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void updatePlanButtonsVisibility() {
        int planCount = 0;
        for (ExercisePlan plan : client.getExercisePlans()) {
            if (planCount >= plans.length) continue;

            JButton planButton = plans[planCount];
            planButton.setVisible(true);
            planButton.setEnabled(true);
            planButton.setText(plan.getName() + " (" + plan.getDate().format(Utils.DATE_FORMATTER) + ")");
            planCount++;
        }

        for (int i = planCount; i < plans.length; i++) {
            plans[i].setVisible(false);
            plans[i].setEnabled(false);
        }
        if (client.getExercisePlans().isEmpty()) {
            plans[0].setText("No hay ningún plan.");
            plans[0].setVisible(true);
            plans[0].setEnabled(false);
        }
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        addPlanButton = new WebButton(Resources.getInstance().PLUS_ICON);
    }
}
