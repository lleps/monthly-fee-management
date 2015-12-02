package com.lleps.mfm.view;

import com.lleps.mfm.Resources;

import javax.swing.*;

/**
 * @author Leandro B. on 03/11/2015.
 */
public class IconedDialog extends JDialog {
    public IconedDialog() {
        setIconImage(Resources.getInstance().APP_IMAGE);
    }
}
