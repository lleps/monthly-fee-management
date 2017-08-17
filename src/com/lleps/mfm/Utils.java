package com.lleps.mfm;

import com.alee.laf.WebLookAndFeel;
import com.lleps.mfm.view.FloatingMessageView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * @author Leandro B. on 01/11/2015.
 */
public class Utils {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String getMonthWithYear(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())
                + " del " + date.getYear();
    }

    public static String firstUpperCase(String string) {
        if (string.length() <= 1) return string.toUpperCase();
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String priceToString(int value) {
        return "$" + NumberFormat.getNumberInstance(Locale.US).format(value);
    }

    public static boolean containsIgnoreCase(String src, String what) {
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, what, 0, length))
                return true;
        }
        return false;
    }

    public static void reportException(Throwable throwable, String description) {
        String errorDescription = description + "\n\n" + "\nStack trace: " + getStackTrace(throwable);

        JOptionPane.showMessageDialog(null, errorDescription);

        System.out.println(description);
        throwable.printStackTrace(System.out);

        String errorsFile = "exceptions.txt";

        try {
            new File(errorsFile).createNewFile();
            Files.write(Paths.get(errorsFile), errorDescription.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("No se puede escribir: " + e);
            e.printStackTrace();
        }
    }

    public static void doUsingNativeLAF(Runnable action) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        action.run();
        WebLookAndFeel.install();
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}