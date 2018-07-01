package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;

public class ReportScreen extends BorderPane {


    public ReportScreen(Data data) throws IOException {

        FXMLLoader loader = new FXMLLoader(ReportScreen.class.getResource("report_toolbar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();


        FXMLLoader loader2 = new FXMLLoader(ReportScreen.class.getResource("product_summary.fxml"));
        loader2.setController(new ProductSummary(data));
        Parent load = loader2.load();

        setCenter(load);

    }

    public void show() {
        Stage stage = new Stage();
        Scene scene = new Scene(this);

        scene.getStylesheets().add("report_styles.css");

        stage.setScene(scene);
        stage.show();
    }

    /**
     * https://carlfx.wordpress.com/2013/07/15/introduction-by-example-javafx-8-printing/
     * https://stackoverflow.com/questions/31231021/javafx8-print-api-how-to-set-correctly-the-printable-area
     */
    @FXML
    private void print() {
        Node node = getCenter();
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout
                = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
        PrinterAttributes attr = printer.getPrinterAttributes();
        PrinterJob job = PrinterJob.createPrinterJob();
        double scaleX
                = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY
                = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        Scale scale = new Scale(scaleX, scaleY);
        node.getTransforms().add(scale);

        if (job != null && job.showPrintDialog(node.getScene().getWindow())) {
            boolean success = job.printPage(pageLayout, node);
            if (success) {
                job.endJob();
            }
        }
        node.getTransforms().remove(scale);
    }

}
