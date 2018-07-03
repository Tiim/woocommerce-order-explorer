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

public class OverviewReportController {

    private final List<OrderedProduct> data;
    @FXML
    private Label title;
    @FXML
    private TableView<OrderedProduct> table;
    @FXML
    private TableColumn<OrderedProduct, String> firstName;
    @FXML
    private TableColumn<OrderedProduct, String> lastName;
    @FXML
    private TableColumn<OrderedProduct, String> itemName;
    @FXML
    private TableColumn<OrderedProduct, String> quantity;
    @FXML
    private TableColumn<OrderedProduct, String> sku;
    @FXML
    private TableColumn<OrderedProduct, String> meta;
    @FXML
    private TableColumn<OrderedProduct, String> price;


    public OverviewReportController(List<OrderedProduct> data) {

        this.data = data;
    }

    @FXML
    private void initialize() {
        title.setText("Order Overview");

        firstName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getOrder().getFirstName()));
        lastName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getOrder().getLastName()));
        itemName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getProduct().getName()));
        quantity.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getProduct().getQuantity())));
        sku.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getProduct().getSku()));
        meta.setCellValueFactory(param -> new SimpleStringProperty(Util.formatMap(param.getValue().getProduct().getMeta())));
        price.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getProduct().getPrice())));

        FXUtil.setWrapTableColumnCellFactory(firstName, lastName, itemName, sku, meta);

        table.setItems(FXCollections.observableArrayList(data));
    }
}
