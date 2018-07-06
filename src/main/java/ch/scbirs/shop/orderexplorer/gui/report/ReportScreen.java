package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.gui.ExceptionAlert;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public abstract class ReportScreen<T> extends BorderPane {

    private static final Logger LOGGER = LogUtil.get();
    private final T data;
    private Stage stage;

    @FXML
    private PDFView pdfView;


    public ReportScreen(T data) throws IOException {
        this.data = data;
        FXMLLoader loader = new FXMLLoader(OrderReport.class.getResource("report_toolbar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    public void show() {
        FXMLLoader loader2 = new FXMLLoader(OrderReport.class.getResource(getFxml()));
        loader2.setController(getNewController(data));
        Parent load = null;
        try {
            load = loader2.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setCenter(load);

        stage = new Stage();
        Scene scene = new Scene(this);

        scene.getStylesheets().add("report_styles.css");

        stage.setScene(scene);

        stage.setResizable(false);

        stage.show();
    }

    protected abstract List<FileChooser.ExtensionFilter> getExtensionFilters();

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
    private void exportImpl() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select where to save the report");
        chooser.getExtensionFilters().addAll(getExtensionFilters());
        File selectedFile = chooser.showSaveDialog(stage);
        if (selectedFile != null) {
            ExceptionAlert.doTry(() -> export(selectedFile.toPath()));
        }
    }

    protected void print(Printer printer) {
        Pane node = getPrintableNode();
        WritableImage snap = node.snapshot(null, new WritableImage((int) node.getPrefWidth(),
                (int) node.getPrefHeight()));

        ImageView image = new ImageView(snap);

        PageLayout pageLayout
                = printer.createPageLayout(Paper.A4, getPrintOrientation(), Printer.MarginType.HARDWARE_MINIMUM);
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        double scaleX
                = pageLayout.getPrintableWidth() / image.getBoundsInParent().getWidth();
        double scaleY
                = pageLayout.getPrintableHeight() / image.getBoundsInParent().getHeight();
        Scale scale = new Scale(scaleX, scaleY);
        image.getTransforms().add(scale);

        if (job.showPrintDialog(node.getScene().getWindow())) {
            boolean success = job.printPage(pageLayout, image);
            if (success) {
                job.endJob();
            }
        }
    }

    protected abstract String getFxml();

    protected abstract Object getNewController(T data);

    protected abstract void export(Path p) throws IOException;

    protected abstract Pane getPrintableNode();

    protected abstract PageOrientation getPrintOrientation();
}
