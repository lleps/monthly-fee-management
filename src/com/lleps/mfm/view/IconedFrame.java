package com.lleps.mfm.view;

import com.lleps.mfm.Resources;

import javax.swing.*;

/**
 * @author Leandro B. on 03/11/2015.
 */
public class IconedFrame extends JFrame {
    public IconedFrame() {
        setIconImage(Resources.getInstance().APP_IMAGE);
    }
}