package com.lleps.mfm.view;

import com.lleps.mfm.Resources;
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

    private ExercisePlan exercisePlan = null;

    private static Map<String, String[][]> planPrototypes = new LinkedHashMap<>();

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

    public AddPlanDialog() {
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
        for (Map.Entry<String, String[][]> exercises : planPrototypes.entrySet()) {
            //create empty columns
            String[][] emptyExercises = getEmptyExercises();
            String[][] prototypeExercises = exercises.getValue();
            if (emptyExercises.length > prototypeExercises.length) {
                for (int i = 0; i < prototypeExercises.length; i++) {
                    for (int j = 0; j < prototypeExercises[i].length; j++) {
                        emptyExercises[i][j] = prototypeExercises[i][j];
                    }
                }
            }

            prototypeComboBox.addItem(new ComboItem(exercises.getKey(), emptyExercises));
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

    static {
        planPrototypes.put("plan 1 (hombre)", new String[][]{
                { "Bicicleta", "10 min", " ", " " },
                { "Extensión", "3", "12", "1 min" },
                { "Camilla flexión", "3", "10", "1 min" },
                { "Pantorrillas", "3", "15", "1 min" },
                { "Press hammer", "3", "15", "1 min" },
                { "Tirones de polea", "3", "12", "1 min" },
                { "Vuelo laterales", "3", "12", "1 min" },
                { "Alternados", "3", "10", "1 min" },
                { "Triceps polea", "3", "12", "1 min" },
                { "Abd sup", "3", " ", " " },
                { "Abd inf", "3", " ", " " },
                { "Puente frontal", "3", " ", " " },
                { "Aeróbico", "10 min", " ", " " }
        });
        planPrototypes.put("plan 1 (mujer)", new String[][]{
                { "Bicicleta", "10 min", " ", " " },
                { "Extension", "3", "12", "1 min" },
                { "Silla aductores", "3", "15", "1 min" },
                { "Abductores en mult.", "3", "12", "1 min" },
                { "Glutera", "3", "12", "1 min" },
                { "Press hammer", "3", "15", "1 min" },
                { "Remo sentado", "3", "12", "1 min" },
                { "Vuelo laterales", "3", "10", "1 min" },
                { "Alternados", "3", "10", "1 min" },
                { "Triceps polea", "3", "15", "1 min" },
                { "Abd sup", "3", " ", " " },
                { "Abd inf", "3", " ", " " },
                { "Puente frontal", "3", " ", " " },
                { "Aerobico", "20 min", " ", " " }
        });
        planPrototypes.put("plan 2 (hombre)", new String[][]{
                { "Bicicleta", "10 min", " ", " " },
                { "Extension a 1 pierna", "3", "10", "1 min" },
                { "Femoral parado", "3", "10", "1 min" },
                { "Prensa 45º", "3", "10", "1 min" },
                { "Tirones de polea", "4", "10", "1 min" },
                { "remo alto", "3", "12", "1 min" },
                { "Triceps c/soga", "4", "12", "1 min" },
                { "Fondo banco", "3", "10", "1 min" },
                { "Puente frontal", "3", " ", " " },
                { "Puente lateral", "3", " ", " " },
                { "DIA II", " ", " ", " " },
                { "Press banca", "4", "10", "1 min" },
                { "Press inclinado", "3", "12", "1 min" },
                { "Apertura", "3", "10", "1 min" },
                { "Militar hammer", "4", "10", "1 min" },
                { "Hombro c/disco", "3", "12", "1 min" },
                { "Curl biceps", "4", "10", "1 min" },
                { "Concentrado", "3", "10", "1 min" },
                { "Abd sup", "4", " ", " " },
                { "Abd inf", "4", " ", " " },
                { "Lumbares", "4", " ", " " }
        });
        planPrototypes.put("plan 2 (mujer)", new String[][]{
                { "Bicicleta", "10 min", " ", " " },
                { "Extension a 1 pier", "3", "12", "1 min" },
                { "Femoral Parado", "3", "12", "1 min" },
                { "Sentadillas", "3", "10", "1 min" },
                { "Prensa de gluteos", "3", "12", "1 min" },
                { "Gluteos polea", "3", "12", "1 min" },
                { "Eliptico", "3 min", " ", " " },
                { "Apertura", "3", "10", "1 min" },
                { "Tirones al pecho", "3", "12", "1 min" },
                { "Curl biceps", "3", "10", "1 min" },
                { "Vuelo combinado", "3", "12", "1 min" },
                { "Curl biceps", "3", "10", "1 min" },
                { "Tricpes c/soga", "3", "15", "1 min" },
                { "Elevacion pelvis", "3", " ", " " },
                { "Puente lat", "3", " ", " " },
                { "Abd rectos", "3", " ", " " },
                { "Aerobico", "15 min", " ", " " }
        });
    }
}
