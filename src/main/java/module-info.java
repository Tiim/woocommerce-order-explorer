
module ch.scbirs.shop.orderexplorer {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires com.google.common;


    exports ch.scbirs.shop.orderexplorer.gui;
    opens ch.scbirs.shop.orderexplorer.gui;
}