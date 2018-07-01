package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.Env;
import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.util.Util;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ProductListCell extends ListCell<Product> {

    private final ObjectProperty<Data> data;
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

    public ProductListCell(ObjectProperty<Data> data) {
        this.data = data;
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

            Path imgPath = OrderExplorer.FOLDER.resolve(data.get().getImage(item));

            try {
                Image value = new Image(Files.newInputStream(imgPath), 200, 200, true, true);
                img.setImage(value);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e);
            }
            name.setText(item.getName());
            price.setText("CHF " + String.valueOf(item.getPrice()));
            quantity.setText(String.valueOf(item.getQuantity()) + "x");
            sku.setText(item.getSku());

            Map<String, String> meta = item.getMeta();
            if (Env.getInstance().debug) {
                meta = new HashMap<>(meta);
                meta.put("id", String.valueOf(item.getId()));
            }

            this.meta.setText(Util.formatMap(meta).toUpperCase());

            setGraphic(root);
        }

    }
}
