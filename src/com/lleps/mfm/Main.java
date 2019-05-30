package com.lleps.mfm;

import com.alee.laf.WebLookAndFeel;
import com.lleps.mfm.view.ClientLoginScreen;
import com.lleps.mfm.view.FloatingMessageView;
import com.lleps.mfm.model.Category;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.util.*;

/**
 * @author Leandro B. on 31/10/2015.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WebLookAndFeel.install();

            FloatingMessageView.show("Cargando categorias...");
            List<Category> categories = Storage.getInstance().loadAllCategories();
            FloatingMessageView.hide();

            // Init login screen switching
            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException e) { /* */ }

            ClientLoginScreen.initLoginScreen(categories);
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

                }

                @Override
                public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
                    System.out.println("alo? " + nativeKeyEvent.getKeyCode());
                    if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
                        SwingUtilities.invokeLater(() -> {
                            if (ClientLoginScreen.checkSwitch()) {
                                ClientLoginScreen.toggle(!ClientLoginScreen.isVisible());
                            }
                        });
                    }
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

                }
            });
            new MainController(categories);
        });
    }
}