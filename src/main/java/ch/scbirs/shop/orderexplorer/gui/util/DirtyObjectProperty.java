package ch.scbirs.shop.orderexplorer.gui.util;

import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.logging.log4j.Logger;

public class DirtyObjectProperty<T> extends SimpleObjectProperty<T> {
    private static final Logger LOGGER = LogUtil.get();
    public SimpleBooleanProperty dirty = new SimpleBooleanProperty(false);

    public DirtyObjectProperty() {
        super();
        init();
    }

    public DirtyObjectProperty(T initialValue) {
        super(initialValue);
        init();
    }

    private void init() {
        addListener((observable, oldValue, newValue) -> {
            dirty.set(true);
        });
    }

    public boolean getDirty() {
        return dirty.get();
    }

    public SimpleBooleanProperty dirtyProperty() {
        return dirty;
    }

    public void resetDirty() {
        dirty.set(false);
    }
}
