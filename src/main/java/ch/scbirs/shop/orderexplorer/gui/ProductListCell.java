package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class ProductListCell extends ListCell<Product> {

    private FXMLLoader loader;

    private Parent root;

    @FXML
    private ImageView img;
    @FXML
    private Label name;
    @FXML
    private Label price;
    @FXML
    private Label quantity;
    @FXML
    private Label sku;
    @FXML
    private Label meta;

    public ProductListCell() {
    }

    @Override
    protected void updateItem(Product item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(ProductListCell.class.getResource("product_list_item.fxml"));
                loader.setController(this);

                try {
                    root = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            img.setImage(null);
            name.setText(item.getName());
            price.setText(String.valueOf(item.getPrice()));
            quantity.setText(String.valueOf(item.getQuantity()));
            sku.setText(item.getSku());
            meta.setText(item.getMeta().toString());

            setGraphic(root);
        }

    }
}
