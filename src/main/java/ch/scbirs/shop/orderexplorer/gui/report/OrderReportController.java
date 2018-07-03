package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.util.FXUtil;
import ch.scbirs.shop.orderexplorer.util.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class OrderReportController {

    private final List<ProductCount> groupedData;


    @FXML
    private Label title;
    @FXML
    private TableView<ProductCount> table;
    @FXML
    private TableColumn<ProductCount, String> number;
    @FXML
    private TableColumn<ProductCount, String> name;
    @FXML
    private TableColumn<ProductCount, String> sku;
    @FXML
    private TableColumn<ProductCount, String> meta;
    @FXML
    private TableColumn<ProductCount, String> price;
    @FXML
    private TableColumn<ProductCount, String> total;

    public OrderReportController(List<ProductCount> data) {
        groupedData = data;
    }

    @FXML
    private void initialize() {
        title.setText("Report");

        number.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getCount())));
        name.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getProduct().getName()));
        sku.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getProduct().getSku()));
        meta.setCellValueFactory(p -> new SimpleStringProperty(Util.formatMap(p.getValue().getProduct().getMeta())));

        price.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getProduct().getPrice())));
        total.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(
                p.getValue().getProduct().getPrice() * p.getValue().getCount()
        )));

        FXUtil.setWrapTableColumnCellFactory(name, sku, meta);

        table.setItems(FXCollections.observableArrayList(groupedData));
    }
}