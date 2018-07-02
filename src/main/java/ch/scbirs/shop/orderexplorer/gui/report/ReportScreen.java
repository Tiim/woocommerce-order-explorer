package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class ReportScreen extends BorderPane {
    private static final Logger LOGGER = LogUtil.get();

    //TODO: export as csv

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

        stage.setResizable(false);

        stage.show();
    }
    /**
     * https://carlfx.wordpress.com/2013/07/15/introduction-by-example-javafx-8-printing/
     * https://stackoverflow.com/questions/31231021/javafx8-print-api-how-to-set-correctly-the-printable-area
     */
    private void print(Printer printer) {
        Node node = getCenter();
        PageLayout pageLayout
                = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
        PrinterAttributes attr = printer.getPrinterAttributes();
        PrinterJob job = PrinterJob.createPrinterJob(printer);
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

    @FXML
    private void print() {
        Printer printer = Printer.getDefaultPrinter();
        print(printer);
    }

    @FXML
    private void printPdf() {
        Optional<Printer> pdf = Printer.getAllPrinters().stream().filter(p -> p.getName().toLowerCase().contains("pdf")).findFirst();
        if (pdf.isPresent()) {
            LOGGER.info("Printer: " + pdf.get().getName());
            print(pdf.get());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No pdf printer found.");
            alert.show();
        }
    }



}
