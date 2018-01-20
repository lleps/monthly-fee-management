package com.lleps.mfm.view;

import com.alee.laf.button.WebButton;
import com.lleps.mfm.Resources;
import com.lleps.mfm.Storage;
import com.lleps.mfm.Utils;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.ExercisePlan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDate;

public class CategoryPlansView extends JDialog {
    private JPanel contentPane;
    private JButton buttonAddPlan;
    private JPanel buttonContainer;
    private Category category;

    public CategoryPlansView(Category category) {
        this.category = category;

        setIconImage(Resources.getInstance().APP_IMAGE);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonAddPlan);
        setTitle("Editar planes");
        setMinimumSize(new Dimension(300, 100));
        buttonAddPlan.addActionListener(e -> onAddPlan());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        updateButtonsByPlans();
        pack();
    }

    private void updateButtonsByPlans() {
        buttonContainer.removeAll();

        if (category.getPlans().isEmpty()) {
            buttonContainer.setLayout(new GridLayout(1, 1));
            JLabel button = new JLabel("No hay ningún plan...");
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setEnabled(false);
            buttonContainer.add(button);
        } else {
            buttonContainer.setLayout(new GridLayout(category.getPlans().size(), 1));
            for (ExercisePlan plan : category.getPlans()) {
                JButton button = new JButton(plan.getName() + " (" + plan.getDate().format(Utils.DATE_FORMATTER) + ")");
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.addActionListener(e -> {
                    Utils.doUsingNativeLAF(() -> {
                        ExercisePlanView planView = new ExercisePlanView(category, null, plan);
                        planView.pack();
                        planView.setLocationRelativeTo(this);
                        planView.setVisible(true);
                    });
                    updateButtonsByPlans();
                });
                buttonContainer.add(button);
            }
        }
        getContentPane().validate();
        getContentPane().repaint();
        pack();
    }

    private void onAddPlan() {
        String newPlanName = (String) JOptionPane.showInputDialog(null,
                "Nombre del plan:",
                "Añadir plan",
                JOptionPane.PLAIN_MESSAGE,
                Resources.getInstance().PLUS_ICON, null,
                "nombre del plan...");
        if (newPlanName != null && !newPlanName.isEmpty()) {
            ExercisePlan plan = new ExercisePlan(newPlanName, LocalDate.now(), getEmptyExercises());
            category.addPlan(plan);
            updateButtonsByPlans();
            try {
                Storage.getInstance().saveCategoryPlans(category);
            } catch (IOException e) {
                Utils.reportException(e, "error saving plans");
            }
        }
    }

    private String[][] getEmptyExercises() {
        String[][] emptyExercises = new String[36][4];
        for (int i = 0; i < emptyExercises.length; i++) {
            for (int j = 0; j < emptyExercises[i].length; j++) {
                emptyExercises[i][j] = "";
            }
        }
        return emptyExercises;
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        buttonAddPlan = new WebButton("Agregar plan", Resources.getInstance().PLUS_ICON);
    }
}