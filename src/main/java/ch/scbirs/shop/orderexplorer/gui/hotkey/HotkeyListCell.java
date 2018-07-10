package ch.scbirs.shop.orderexplorer.gui.hotkey;

import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCombination;
import org.apache.commons.lang3.tuple.Pair;

public class HotkeyListCell extends ListCell<Pair<String, KeyCombination>> {

    @Override
    protected void updateItem(Pair<String, KeyCombination> item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            KeyCombination kc = item.getRight();
            setText(item.getLeft() + " - [" + (kc != null ? kc.getDisplayText() : " ") + "]");
        }
    }
}
