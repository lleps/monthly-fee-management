package com.lleps.mfm;

import com.lleps.mfm.view.MainView;
import com.lleps.mfm.model.Category;
import org.apache.commons.lang3.StringUtils;

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
                            new ArrayList<>(), new ArrayList<>());

                    Storage.getInstance().saveCategory(category);
                    addCategory(category);
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