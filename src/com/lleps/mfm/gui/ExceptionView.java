package com.lleps.mfm.gui;

import com.lleps.mfm.Storage;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Leandro B. on 04/11/2015.
 */
public class ExceptionView {
    public static void show(String description, Throwable throwable) {
        String errorDescription = description + "\n\n" + getStackTrace(throwable);

        System.out.println(description);
        throwable.printStackTrace(System.out);

        Storage.getInstance().appendException(errorDescription);

        JOptionPane.showMessageDialog(null, errorDescription);
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
