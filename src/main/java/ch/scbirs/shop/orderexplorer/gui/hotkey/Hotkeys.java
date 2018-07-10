package ch.scbirs.shop.orderexplorer.gui.hotkey;

import ch.scbirs.shop.orderexplorer.util.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.scene.input.KeyCode;
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
import java.util.TreeSet;

public class Hotkeys {

    private static final Logger LOGGER = LogUtil.get();
    private static Hotkeys INSTANCE;
    private final Map<String, KeyCombination> keyMap = new HashMap<>();
    private final Map<String, KeyCombination> defaultMap = new HashMap<>();
    private final Map<String, HotkeyAction> actionMap = new HashMap<>();
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

    Set<String> getKeys() {
        Set<String> strings = defaultMap.keySet();
        return new TreeSet<>(strings);
    }

    KeyCombination getKeyCombination(String s) {
        if (keyMap.containsKey(s)) {
            return keyMap.get(s);
        } else {
            return defaultMap.get(s);
        }
    }

    void overwrite(String key, KeyCombination c) {
        if (!defaultMap.get(key).equals(c)) {
            keyMap.put(key, c);
        } else {
            keyMap.remove(key);
        }
        try {
            save(file);
        } catch (IOException e) {
            LOGGER.warn("Can't save keymap", e);
        }
    }

    void delete(String key) {
        keyMap.remove(key);
        try {
            save(file);
        } catch (IOException e) {
            LOGGER.warn("Can't save keymap", e);
        }
    }

    public boolean match(String s, KeyEvent event) {
        if (keyMap.containsKey(s)) {
            return keyMap.get(s).match(event);
        } else {
            return defaultMap.get(s).match(event);
        }
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
            array.add(node);
        }
        om.writeValue(p.toFile(), array);
    }

    public void keymap(String key, KeyCombination combination, HotkeyAction callback) {
        defaultMap.put(key, combination);
        actionMap.put(key, callback);
    }

    public void keymap(String key, KeyCode code, HotkeyAction callback) {
        keymap(key, new KeyCodeCombination(code), callback);
    }

    public void execute(KeyEvent event) {
        for (String key : defaultMap.keySet()) {
            Map<String, KeyCombination> map;
            if (keyMap.containsKey(key)) {
                map = keyMap;
            } else {
                map = defaultMap;
            }
            if (map.get(key).match(event)) {
                runAction(key);
            }
        }
    }

    private void runAction(String key) {
        try {
            actionMap.get(key).run();
        } catch (Exception e) {
            throw new RuntimeException("Error during hotkey action", e);
        }
    }

    public interface HotkeyAction {
        void run() throws Exception;
    }
}
