package com.lleps.mfm;

import com.lleps.mfm.model.Client;
import com.lleps.mfm.view.ClientLoginScreen;
import com.lleps.mfm.view.FloatingMessageView;
import com.lleps.mfm.view.MainView;
import com.lleps.mfm.model.Category;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author Leandro B. on 01/11/2015.
 */
public class MainController {
    MainView view;
    List<Category> categories;
    List<CategoryController> categoryControllers = new ArrayList<>();

    MainController(List<Category> categories) {
        this.categories = categories;
        ClientLoginScreen.initLoginScreen(categories);

        view = new MainView();
        view.setMinimumSize(new Dimension(512, 512));
        view.setTitle("Gestión de cuotas de clientes");
        view.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                view.setVisible(false);
                FloatingMessageView.show("Haciendo backup de los datos...");
                try {
                    Storage.getInstance().doBackup();
                } catch (IOException exception) {
                    Utils.reportException(exception, "Error haciendo backup.");
                }
                FloatingMessageView.hide();
                System.exit(0);
            }
        });
        view.setExtendedState(view.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        view.setVisible(true);
        categories.forEach(this::addCategory);

        view.setNewCategoryButtonListener(e -> {
            selectLastTab();

            String newCategoryName = (String) JOptionPane.showInputDialog(null,
                    "Escribe el nombre de la categoria",
                    "Crear nueva categoria",
                    JOptionPane.PLAIN_MESSAGE,
                    Resources.getInstance().PLUS_ICON, null,
                    "Nueva categoria");

            if (newCategoryName != null) {
                for (Category c : categories) {
                    if (c.getName().equalsIgnoreCase(newCategoryName)) {
                        JOptionPane.showMessageDialog(null,
                                "Ya existe una categoría con ese nombre." +
                                        "\nPuede borrarla haciendo click derecho sobre ella.",
                                "Nombre inválido",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                if (!StringUtils.isAlphanumericSpace(newCategoryName)) {
                    JOptionPane.showMessageDialog(null,
                            "Sólo puede usar caracteres a-z 0-9 en el nombre de una categoría.",
                            "Nombre inválido",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    Category category = new Category(newCategoryName,
                            0,
                            new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

                    Storage.getInstance().saveCategory(category);
                    addCategory(category);
                    categories.add(category);
                } catch (IOException e2) {
                    Utils.reportException(e2, "Error guardando la categoría.\n" +
                            "Asegurese que el nombre no contenga caracteres diferentes de a-z 0-9 ");
                }


                selectLastTab();
            }
        });

        view.setDeleteCategoryListener(e -> {
            view.getSelectedCategory().ifPresent(categoryView -> {
                Category category = null;
                for (CategoryController cc : categoryControllers) {
                    if (cc.getView() == categoryView) {
                        category = cc.getCategory();
                    }
                }
                if (category != null) {
                    int choosedOption = JOptionPane.showConfirmDialog(null, "La categoría " + category.getName() + " contiene " + category.getClients().size() + " clientes "
                    + " y " + category.getPayments().size() + " registros de pagos.\n\n" +
                            "¿Está seguro que desea eliminarla? Esta acción es irreversible.",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (choosedOption == JOptionPane.YES_OPTION) {
                        view.removeCategory(categoryView);
                        Storage.getInstance().removeCategory(category);
                        categories.remove(category);
                        Category finalCategory = category;
                        categoryControllers.removeIf(controller -> controller.category == finalCategory);
                    }
                }
            });
        });
    }

    private void addCategory(Category category) {
        CategoryController categoryController = new CategoryController(category);
        view.addCategory(categoryController.getView());
        categoryControllers.add(categoryController);
    }

    private void selectLastTab() {
        if (!categoryControllers.isEmpty()) {
            view.setSelectedCategory(categoryControllers.get(categoryControllers.size() - 1).getView());
        }
    }
}