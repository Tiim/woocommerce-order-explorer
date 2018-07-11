package ch.scbirs.shop.orderexplorer.gui;

import javafx.scene.image.Image;

public class Icons {

    private static final String[] FILES = {"logo.png"};
    private static Image[] ICONS = null;

    public static Image[] getIcons() {
        if (ICONS == null) {
            ICONS = new Image[FILES.length];
            for (int i = 0; i < FILES.length; i++) {
                ICONS[i] = new Image(Icons.class.getResourceAsStream(FILES[i]));
            }
        }
        return ICONS;
    }

}
