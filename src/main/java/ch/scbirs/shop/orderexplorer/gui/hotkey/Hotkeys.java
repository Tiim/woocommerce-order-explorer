package ch.scbirs.shop.orderexplorer.gui.hotkey;

import ch.scbirs.shop.orderexplorer.util.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static javafx.scene.input.KeyCombination.ModifierValue.DOWN;
import static javafx.scene.input.KeyCombination.ModifierValue.UP;

public class Hotkeys {

    private static final Logger LOGGER = LogUtil.get();
    private static Hotkeys INSTANCE;
    private final Map<String, KeyCombination> keyMap = new HashMap<>();
    private final Path file;

    private Hotkeys(Path file) {
        this.file = file;
        try {
            load(file);
        } catch (IOException e) {
            LOGGER.warn("Can't load key map", e);
        }
    }

    public static Hotkeys getInstance() {
        return INSTANCE;
    }

    public static void init(Path file) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Instance already initialized");
        }
        INSTANCE = new Hotkeys(file);
    }

    public static KeyCodeCombination getKCCFromEvent(KeyEvent e) {
        if (e.getCode().isModifierKey()) {
            return null;
        }
        return new KeyCodeCombination(
                e.getCode(),
                e.isShiftDown() ? DOWN : UP,
                e.isControlDown() ? DOWN : UP,
                e.isAltDown() ? DOWN : UP,
                e.isMetaDown() ? DOWN : UP,
                e.isShortcutDown() ? DOWN : UP
        );
    }

    Set<String> getKeys() {
        return keyMap.keySet();
    }

    KeyCombination getKeyCombination(String s) {
        return keyMap.get(s);
    }

    private void load(Path p) throws IOException {
        if (!Files.exists(p)) {
            return;
        }

        ObjectMapper om = new ObjectMapper();
        JsonNode json = om.readTree(p.toFile());
        json.forEach(node -> {
            String key = node.get("key").asText();
            String name = node.get("name").asText();
            keyMap.put(name, KeyCodeCombination.valueOf(key));
        });
    }

    private void save(Path p) throws IOException {
        ObjectMapper om = new ObjectMapper();

        ArrayNode array = om.createArrayNode();
        for (String s : keyMap.keySet()) {
            KeyCombination kc = keyMap.get(s);
            ObjectNode node = om.createObjectNode();
            node.set("key", new TextNode(kc.getName()));
            node.set("name", new TextNode(s));
        }
        om.writeValue(p.toFile(), array);
    }

    public void changeBinding(String key, KeyCombination combination) {
        keyMap.put(key, combination);
        try {
            save(file);
        } catch (IOException e) {
            LOGGER.warn("Can't save keymap", e);
        }
    }

}
