package com.lleps.mfm.gui;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

public class LabeledPopupMenu extends JPopupMenu
{
    private String originalLabelText = null;
    private final JLabel label;

    private static String replaceHTMLEntities(String text)
    {
        if (-1 != text.indexOf("<") ||
                -1 != text.indexOf(">") ||
                -1 != text.indexOf("&"))
        {
            text = text.replaceAll("&", "&amp;");
            text = text.replaceAll("<", "&lt;");
            text = text.replaceAll(">", "&gt;");
        }
        return text;
    }

    public LabeledPopupMenu()
    {
        super();
        this.label = null;
    }

    public LabeledPopupMenu(String label)
    {
        super();
        originalLabelText = label;
        this.label = new JLabel("<html><b>" +
                replaceHTMLEntities(label) + "</b></html>");
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        add(this.label);
        addSeparator();
    }

    @Override public void setLabel(String text)
    {
        if (null == label) return;
        originalLabelText = text;
        label.setText("<html><b>" +
                replaceHTMLEntities(text) +
                "</b></html>");
    }

    @Override public String getLabel()
    {
        return originalLabelText;
    }
}