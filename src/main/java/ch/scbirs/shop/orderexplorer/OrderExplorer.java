package ch.scbirs.shop.orderexplorer;


import ch.scbirs.shop.orderexplorer.gui.Gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OrderExplorer {

    public static final Path FOLDER = Paths.get("data");
    public static final Path SETTINGS_FILE = FOLDER.resolve("savefile.json");

    public static void main(String[] args) throws IOException {
        Files.createDirectories(FOLDER);

        Gui.run(args);
    }

}
