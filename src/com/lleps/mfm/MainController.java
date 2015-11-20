package com.lleps.mfm;

import com.lleps.mfm.gui.MainView;
import com.lleps.mfm.model.Category;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author Leandro B. on 01/11/2015.
 */
public class MainController {
    MainView view;
    List<CategoryController> categoryControllers = new ArrayList<>();

    MainController(List<Category> categories) {
        view = new MainView();
        view.setTitle("Gestión de cuotas de clientes");
        view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
                Category category = new Category(newCategoryName,
                        0,
                        new ArrayList<>(), new ArrayList<>());

                addCategory(category);

                try {
                    Storage.getInstance().saveCategory(category);
                } catch (IOException e2) {
                    Utils.reportException(e2, "Error guardando la categoría");
                }

                selectLastTab();
            }
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