package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

public class ProductSummary {

    private Data data;

    @FXML
    private Label title;
    @FXML
    private TableColumn number;
    @FXML
    private TableColumn name;
    @FXML
    private TableColumn sku;
    @FXML
    private TableColumn meta;
    @FXML
    private TableColumn price;
    @FXML
    private TableColumn total;

    @FXML
    private void initialize() {
        title.setText("Report");
        System.out.println("init");
    }

    public void setData(Data data) {
        this.data = data;
        System.out.println("set data");
    }
}
