
module ch.scbirs.shop.orderexplorer {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires com.google.common;
    requires org.apache.commons.io;


    opens ch.scbirs.shop.orderexplorer.gui to javafx.fxml, javafx.graphics;
    opens ch.scbirs.shop.orderexplorer.model to com.fasterxml.jackson.databind;
}