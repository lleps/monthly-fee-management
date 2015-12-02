package com.lleps.mfm;

import com.alee.laf.WebLookAndFeel;
import com.lleps.mfm.view.FloatingMessageView;
import com.lleps.mfm.model.Category;

import javax.swing.*;
import java.util.*;

/**
 * @author Leandro B. on 31/10/2015.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WebLookAndFeel.install();

            FloatingMessageView.show("Cargando categorias..");
            List<Category> categories = Storage.getInstance().loadAllCategories();
            FloatingMessageView.hide();

            new MainController(categories);
        });
    }
}