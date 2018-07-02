package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.Util;
import ch.scbirs.shop.orderexplorer.util.export.ExcelExporter;
import ch.scbirs.shop.orderexplorer.util.export.Exporter;
import ch.scbirs.shop.orderexplorer.util.export.TsvExporter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportScreen extends BorderPane {
    private static final Logger LOGGER = LogUtil.get();

    private final List<ProductCount> group;
    private Stage stage;


    public ReportScreen(Data data) throws IOException {

        FXMLLoader loader = new FXMLLoader(ReportScreen.class.getResource("report_toolbar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();


        FXMLLoader loader2 = new FXMLLoader(ReportScreen.class.getResource("product_summary.fxml"));
        group = group(data);
        loader2.setController(new ProductSummary(group));
        Parent load = loader2.load();

        setCenter(load);

    }

    private List<ProductCount> group(Data data) {
        List<Product> products = data.getOrders()
                .stream()
                .flatMap(o -> o.getProducts().stream())
                .collect(Collectors.toList());

        List<ProductCount> counts = new ArrayList<>();

        for (Product p : products) {

            Optional<ProductCount> first = counts.stream().filter(pc -> pc.same(p)).findFirst();
            if (!first.isPresent()) {
                counts.add(new ProductCount(p));
            } else {
                first.get().inc(p.getQuantity());
            }
        }
        return counts;
    }

    public void show() {
        stage = new Stage();
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

    @FXML
    private void export() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select where to save the report");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel File", "*.xls"),
                new FileChooser.ExtensionFilter("Tab Separated Values", "*.tsv")
        );
        File selectedFile = chooser.showSaveDialog(stage);
        if (selectedFile != null) {
            String ext = FilenameUtils.getExtension(selectedFile.toString());
            Exporter exp = null;
            String[] header = new String[]{"Nr", "Name", "SKU", "Meta", "Price", "Total"};
            switch (ext) {
                case "tsv":
                    exp = new TsvExporter(header);
                    break;
                case "xls":
                    exp = new ExcelExporter(header);
                default:
                    LOGGER.warn("Unknown extension " + ext);
            }
            export(selectedFile.toPath(), exp);
        }
    }

    private void export(Path p, Exporter exp) throws IOException {
        group.forEach(pc -> exp.addData(
                pc.getCount(),
                pc.getProduct().getName(),
                pc.getProduct().getSku(),
                Util.formatMap(pc.getProduct().getMeta()).replace('\n', ','),
                pc.getProduct().getPrice(),
                pc.getProduct().getPrice() * pc.getCount()
                )
        );
        exp.save(p);
    }

}
