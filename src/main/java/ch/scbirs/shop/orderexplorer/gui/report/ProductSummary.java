package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.util.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductSummary {

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

    public ProductSummary(Data data) {
        groupedData = group(data);
    }

    private List<ProductCount> group(Data data) {
        List<Product> products = data.getOrders()
                .stream()
                .flatMap(o -> o.getProducts().stream())
                .collect(Collectors.toList());

        List<ProductCount> counts = new ArrayList<>();

        for (Product p : products) {

            int i = counts.indexOf(p);
            if (i < 0) {
                counts.add(new ProductCount(p));
            } else {
                counts.get(i).inc();
            }
        }
        return counts;
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

        table.setItems(FXCollections.observableArrayList(groupedData));
    }
}
