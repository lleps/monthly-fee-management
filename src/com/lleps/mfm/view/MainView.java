package com.lleps.mfm.view;

import com.lleps.mfm.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Leandro B. on 31/10/2015..
 */
public class MainView extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private List<CategoryView> categoryViews = new ArrayList<>();
    private ActionListener newCategoryListener;
    private ActionListener deleteCategoryListener;
    private boolean updatingTabs;

    public MainView() {
        setContentPane(mainPanel);
        setIconImage(Resources.getInstance().APP_IMAGE);
        tabbedPane.addChangeListener(l -> {
            if (!updatingTabs) {
                if (tabbedPane.getSelectedIndex() == tabbedPane.getTabCount() - 1) {
                    if (newCategoryListener != null) {
                        newCategoryListener.actionPerformed(new ActionEvent(MainView.this, 0, "click"));
                    }
                }
            }
        });
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    getSelectedCategory().ifPresent(cv -> {
                        JPopupMenu popupMenu = new LabeledPopupMenu(cv.getName());
                        JMenuItem deleteCategoryItem = new JMenuItem("Eliminar", Resources.getInstance().TRASH_ICON);
                        deleteCategoryItem.addActionListener(e1 -> {
                            if (deleteCategoryListener != null) {
                                deleteCategoryListener.actionPerformed(e1);
                            }
                        });
                        popupMenu.add(deleteCategoryItem);
                        popupMenu.show(tabbedPane, tabbedPane.getMousePosition().x, tabbedPane.getMousePosition().y);
                    });
                }
            }
        });

        updateTabs();
    }

    public void addCategory(CategoryView categoryView) {
        categoryViews.add(categoryView);
        updateTabs();
    }

    public void removeCategory(CategoryView categoryView) {
        categoryViews.remove(categoryView);
        updateTabs();
    }

    public boolean hasCategory(CategoryView categoryView) {
        return categoryViews.contains(categoryView);
    }

    public Optional<CategoryView> getSelectedCategory() {
        try {
            return Optional.of(categoryViews.get(tabbedPane.getSelectedIndex()));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public void setSelectedCategory(CategoryView categoryView) {
        int index = categoryViews.indexOf(categoryView);
        if (index != -1) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    public void setNewCategoryButtonListener(ActionListener listener) {
        this.newCategoryListener = listener;
    }

    public void setDeleteCategoryListener(ActionListener deleteCategoryListener) {
        this.deleteCategoryListener = deleteCategoryListener;
    }

    private void updateTabs() {
        updatingTabs = true;
        tabbedPane.removeAll();
        for (CategoryView cv : categoryViews) {
            tabbedPane.addTab(cv.getName(), cv);
        }
        JButton button = new JButton(categoryViews.size() == 0 ? "Click aquí para agregar la primer categoría." : "+");
        button.addActionListener(e -> {
            if (newCategoryListener != null) {
                newCategoryListener.actionPerformed(e);
            }
        });
        tabbedPane.addTab("", Resources.getInstance().PLUS_ICON, button);
        updatingTabs = false;
    }
}