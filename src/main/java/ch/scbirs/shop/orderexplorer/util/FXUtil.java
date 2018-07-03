package ch.scbirs.shop.orderexplorer.util;

import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;
import javafx.util.Callback;


public class FXUtil {

    private static final Callback<TableColumn, TableCell> CELL_FACTORY = param -> {
        TableCell<Object, String> cell = new TableCell<>();
        Text text = new Text();
        cell.setGraphic(text);
        cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
        text.wrappingWidthProperty().bind(cell.widthProperty());
        text.textProperty().bind(cell.itemProperty());
        return cell;
    };

    public static void setWrapTableColumnCellFactory(TableColumn tc) {
        tc.setCellFactory(CELL_FACTORY);
    }

    public static void setWrapTableColumnCellFactory(TableColumn... tc) {
        for (TableColumn t : tc) {
            setWrapTableColumnCellFactory(t);
        }
    }
}
