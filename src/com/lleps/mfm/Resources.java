package com.lleps.mfm;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * @author fedecas and leandro on 26/10/2015..
 */
public class Resources {
    private final static Resources instance = new Resources();

    public static Resources getInstance() {
        return instance;
    }

    public final Image APP_IMAGE = getImageFromResources("app.png", new Dimension(50, 50));

    public final Icon ADDUSER_ICON = getIconFromResources("adduser.png", new Dimension(25, 25));
    public final Icon BAN_ICON = getIconFromResources("ban.png", new Dimension(25, 25));
    public final Icon BROWSE_ICON = getIconFromResources("browse.png", new Dimension(25, 25));
    public final Icon DOLLAR_ICON = getIconFromResources("dollar.png", new Dimension(25, 25));
    public final Icon EYE_ICON = getIconFromResources("eye.png", new Dimension(25, 25));
    public final Icon GRAPHIC_ICON = getIconFromResources("graphic.png", new Dimension(25, 25));
    public final Icon PAYMENT_ICON = getIconFromResources("payment.png", new Dimension(25, 25));
    public final Icon POCKERFACE_ICON = getIconFromResources("pockerface.png", new Dimension(25, 25));
    public final Icon REMOVEUSER_ICON = getIconFromResources("removeuser.png", new Dimension(25, 25));
    public final Icon TRASH_ICON = getIconFromResources("trash.png", new Dimension(25, 25));
    public final Icon PLUS_ICON = getIconFromResources("plus.png", new Dimension(25, 25));
    public final Icon PENCIL_ICON = getIconFromResources("pencil.png", new Dimension(25, 25));
    public final Icon HOME_ICON = getIconFromResources("home.png", new Dimension(25, 25));
    public final Icon PHONE_ICON = getIconFromResources("phone.png", new Dimension(25, 25));
    public final Icon USER_ICON = getIconFromResources("user.png", new Dimension(25, 25));
    public final Icon BLANK_ICON = getIconFromResources("blank.png", new Dimension(2, 25));
    public final Icon MAIL_ICON = getIconFromResources("mail.png", new Dimension(25, 25));
    public final Icon LOADING_ICON = getIconFromResources("loading.gif", new Dimension(25, 25));

    private final static String RESOURCES_PATH = "/resources/";

    private Icon getIconFromResources(String path, Dimension scale) {
        path = RESOURCES_PATH + path;
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            ImageIcon imageIcon = new ImageIcon(imgURL);
            return new ImageIcon(imageIcon.getImage().getScaledInstance(scale.width, scale.height, Image.SCALE_SMOOTH));
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private Image getImageFromResources(String path, Dimension scale) {
        path = RESOURCES_PATH + path;
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage().getScaledInstance(scale.width, scale.height, Image.SCALE_SMOOTH);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}