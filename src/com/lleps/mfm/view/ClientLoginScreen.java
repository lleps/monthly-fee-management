package com.lleps.mfm.view;

import com.lleps.mfm.Utils;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.Payment;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class ClientLoginScreen {
    private static final int MILLIS_TO_SHOW_ASTERISK = 500;
    private static final int ID_LENGTH = 8;
    private static final Color COLOR_CLEAR = new Color(0, 0, 0, 0);
    private static final Color ERROR_BACKGROUND = Color.decode("#121212");
    private static final Color SUCCESS_BACKGROUND = Color.decode("#fce404");
    private static final Color DEFAULT_BACKGROUND = Color.decode("#363636");
    private JLabel idLabel;
    private JPanel rootPanel;
    private JPanel transparentPanel1;
    private JPanel transparentPanel2;
    private JLabel titleLabel;
    private int id;
    private long[] numSlotPressTimestamp = new long[ID_LENGTH]; //to show the number and a * quickly
    private long colorRedExpiry = Long.MAX_VALUE;
    private Robot hal = null;

    public ClientLoginScreen() {
        setIdLabel(0);
        transparentPanel1.setBackground(COLOR_CLEAR);
        transparentPanel2.setBackground(COLOR_CLEAR);
        try {
            hal = new Robot();
        } catch (AWTException e) {
            Utils.reportException(e, "error in new Robot()");
        }
        Timer timer = new Timer(200, evt -> {
            setIdLabel(this.id);
            if (System.currentTimeMillis() > colorRedExpiry) {
                setColorWithTransition(rootPanel, DEFAULT_BACKGROUND, 0.5f);
                //rootPanel.setBackground(DEFAULT_BACKGROUND);
                colorRedExpiry = Long.MAX_VALUE;
                this.id = 0;
                titleLabel.setText("Ingresa tu DNI");
            }


            // Move the mouse around to prevent entering sleep move, as this screen must stay awake
            if (Math.random() < 0.01f) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                Random random = new Random();
                hal.mouseMove((int)p.getX() + random.nextInt(5) - 2, (int)p.getY() + random.nextInt(5) - 2);
            }
        });
        rootPanel.setBackground(DEFAULT_BACKGROUND);
        timer.setRepeats(true);
        timer.start();
    }

    // TODO: some way to stop the timer

    private void onEnterId(int id) {
        Integer daysToExpire = checkDaysForDniExpiry(id);
        System.out.println("id: " + id);
        if (daysToExpire == null) {
            setColorWithTransition(rootPanel, ERROR_BACKGROUND, 0.5f);
            titleLabel.setText("El DNI " + id + " no está registrado.");
            Timer t = new Timer(500, (e) -> playSound("error"));
            t.setRepeats(false);
            t.start();
        } else if (daysToExpire < 0) {
            setColorWithTransition(rootPanel, ERROR_BACKGROUND, 0.5f);
            titleLabel.setText("Tu cuota venció hace " + -daysToExpire + " dias.");
            Timer t = new Timer(500, (e) -> playSound("error"));
            t.setRepeats(false);
            t.start();
        } else if (daysToExpire == Integer.MAX_VALUE) {
            setColorWithTransition(rootPanel, ERROR_BACKGROUND, 0.5f);
            titleLabel.setText("Este DNI aun no pago cuotas.");
            Timer t = new Timer(500, (e) -> playSound("error"));
            t.setRepeats(false);
            t.start();
        } else {
            setColorWithTransition(rootPanel, SUCCESS_BACKGROUND, 0.5f);
            playSound("success");
            titleLabel.setText("Tu cuota vence en " + daysToExpire + " días.");
        }
        colorRedExpiry = System.currentTimeMillis() + 3500;
    }

    private ArrayList<Category> categoryList;

    public void setCategoryList(ArrayList<Category> categoryList) {
        this.categoryList = categoryList;
    }

    private Integer checkDaysForDniExpiry(int dni) {
        Payment lastPayment = null;
        boolean registeredButNeverPaidAnything = false;
        for (Category category : categoryList) {
            Optional<Client> client = category
                    .getClients()
                    .stream()
                    .filter(theClient -> {
                        return theClient.getDni() == dni;
                    })
                    .findFirst();

            if (!client.isPresent()) continue; // doesn't exists in this category
            int clientId = client.get().getId();
            Optional<Payment> categoryLastPayment = category.getPayments()
                    .stream()
                    .filter(payment -> payment.getClientId() == clientId)
                    .max((p1, p2) -> (int)(p1.getMonthDate().toEpochDay() - p2.getMonthDate().toEpochDay()));
            if (!categoryLastPayment.isPresent()) {
                registeredButNeverPaidAnything = true;
                continue;
            }
            Payment categoryPayment = categoryLastPayment.get();
            if (lastPayment == null || (categoryPayment.getMonthDate().toEpochDay() > lastPayment.getMonthDate().toEpochDay())) {
                lastPayment = categoryPayment;
            }
        }
        if (lastPayment == null) {
            if (registeredButNeverPaidAnything) {
                return Integer.MAX_VALUE;
            }
            return null;
        }
        LocalDate expiry = lastPayment.getMonthDate().plusMonths(1);
        LocalDate now = LocalDate.now();
        return (int) (expiry.toEpochDay() - now.toEpochDay());
    }

    void reportKeyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (Character.isDigit(c)) {
            String currentNumber = Integer.toString(id);
            if ("0".equals(currentNumber)) currentNumber = "";
            currentNumber += c;
            if (currentNumber.length() > ID_LENGTH) return;
            // grab keystroke timestamp to know when to show the *
            numSlotPressTimestamp[currentNumber.length() - 1] = System.currentTimeMillis();
            this.id = Integer.parseInt(currentNumber);
            setIdLabel(id);
            if (currentNumber.length() == ID_LENGTH) onEnterId(id);
        }
    }

    void reportKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            this.id = 0;
            setIdLabel(0);
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.id = 0;
            setIdLabel(0);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private static class FloatWrapper {
        float value;
        FloatWrapper(float value) {
            this.value = value;
        }
    }

    private void setColorWithTransition(JComponent component, Color finalColor, float seconds) {
        FloatWrapper wrapper = new FloatWrapper(0f); // progress in transition, from 0 to 1
        Color initialColor = component.getBackground();
        float frequency = 0.016f; // millis
        float wrapperDelta = 1f / (seconds / frequency);
        Timer timer = new Timer((int)(frequency * 1000f), evt -> {
            float ratio = 1f - wrapper.value;
            int red = (int)Math.abs((ratio * initialColor.getRed()) + ((1 - ratio) * finalColor.getRed()));
            int green = (int)Math.abs((ratio * initialColor.getGreen()) + ((1 - ratio) * finalColor.getGreen()));
            int blue = (int)Math.abs((ratio * initialColor.getBlue()) + ((1 - ratio) * finalColor.getBlue()));
            component.setBackground(new Color(red, green, blue));
            wrapper.value += wrapperDelta;
            if (wrapper.value >= 0.99) {
                component.setBackground(finalColor);
                ((Timer)evt.getSource()).stop();
                //System.out.println("stop! wrapper.value is " + wrapperDelta + " + " + wrapper.value);
            }
            //System.out.println("ratio: " + ratio);
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void setIdLabel(int id) {
        // should fill like
        // * * * _ _ _
        // so, fill asterisks
        // then, _ for the remaining digits.
        StringBuilder s = new StringBuilder();
        int numberCount = 0;
        if (id > 0) {
            long now = System.currentTimeMillis();
            String numString = Integer.toString(id);
            for (int i = 0; i < numString.length(); i++) {
                // show an asterisk * after the key input
                if (now - numSlotPressTimestamp[i] > MILLIS_TO_SHOW_ASTERISK) {
                    s.append('*');
                } else {
                    char c = numString.charAt(i);
                    s.append(c);
                }

                numberCount++;
                s.append(" ");
            }
        }
        while (numberCount < ID_LENGTH) {
            numberCount++;
            s.append("_ ");
        }
        idLabel.setText(s.toString());
        rootPanel.repaint();
    }

    // Implemented statically to avoid passing too many dependencies, since this is a
    // whole frame and not just a pane.
    // Caller should call initLoginScreen and toggle. Everything else
    // is done here (i.e view cancellation is done here)

    private static JFrame frame;
    private static ClientLoginScreen screen;
    private static long lastSwitch = 0L;

    public static boolean checkSwitch() {
        if (System.currentTimeMillis() - lastSwitch > 250) {
            lastSwitch = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public static boolean isVisible() {
        return frame != null && frame.isVisible();
    }

    public static void initLoginScreen(java.util.List<Category> categoryList) {
        screen = new ClientLoginScreen();
        screen.setCategoryList(new ArrayList<>(categoryList));
    }

    public static void toggle(boolean toggle) {
        if (frame == null) {
            frame = new JFrame();
            frame.setContentPane(screen.rootPanel);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            frame.setFocusable(true);
            frame.setAlwaysOnTop(true);
            frame.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    screen.reportKeyTyped(e);
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    screen.reportKeyPressed(e); // enter and delete are not detected in keyTyped.
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });
        }
        frame.setVisible(toggle);
    }

    public static void main(String[] args) throws IOException {
        initLoginScreen(new ArrayList<>());
        toggle(true);
    }

    private static void playSound(String sound) {
        new Thread(() -> {
            try {
                AudioStream audio = new AudioStream(ClientLoginScreen.class.getResourceAsStream("/resources/" + sound + ".wav"));
                AudioPlayer.player.start(audio);
            } catch (IOException e) {
                Utils.reportException(e, "play sound " + sound);
            }
        }).start();
    }
}
