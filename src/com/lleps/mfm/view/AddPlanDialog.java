package com.lleps.mfm.view;

import com.lleps.mfm.Resources;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.ExercisePlan;

import javax.swing.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;

public class AddPlanDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameField;
    private JComboBox<ComboItem> prototypeComboBox;
    private Category category;
    private ExercisePlan exercisePlan = null;

    private static class ComboItem {
        private String name;
        private String[][] content;

        ComboItem(String name, String[][] content) {
            this.name = name;
            this.content = content;
        }

        String[][] getContent() {
            return content;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public AddPlanDialog(Category category) {
        this.category = category;
        setIconImage(Resources.getInstance().APP_IMAGE);
        setTitle("Agregar nuevo plan");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pack();

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        prototypeComboBox.removeAllItems();

        prototypeComboBox.addItem(new ComboItem("Vacía", getEmptyExercises()));
        for (ExercisePlan plan : category.getPlans()) {
            prototypeComboBox.addItem(new ComboItem(plan.getName(), plan.getExercises().clone()));
        }

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public Optional<ExercisePlan> getExercisePlan() {
        return Optional.ofNullable(exercisePlan);
    }

    private void onOK() {
        if (nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe escribir un nombre de plan.");
            return;
        }
        ComboItem selectedItem = (ComboItem) prototypeComboBox.getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de plan.");
            return;
        }
        exercisePlan = new ExercisePlan(nameField.getText(), LocalDate.now(), selectedItem.getContent());
        dispose();
    }

    private void onCancel() {
        exercisePlan = null;
        dispose();
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
}
