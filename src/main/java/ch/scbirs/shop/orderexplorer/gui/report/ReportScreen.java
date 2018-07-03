package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.gui.ExceptionAlert;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.Printer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class ReportScreen<T> extends BorderPane {

    private static final Logger LOGGER = LogUtil.get();
    private final T data;
    private List<FileChooser.ExtensionFilter> filters = Collections.emptyList();
    private Stage stage;


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

    protected void setExtensionFilters(List<FileChooser.ExtensionFilter> filters) {

        this.filters = filters;
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
    private void exportImpl() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select where to save the report");
        chooser.getExtensionFilters().addAll(
                filters
        );
        File selectedFile = chooser.showSaveDialog(stage);
        if (selectedFile != null) {
            ExceptionAlert.doTry(() -> export(selectedFile.toPath()));
        }
    }

    protected abstract String getFxml();

    protected abstract Object getNewController(T data);

    protected abstract void print(Printer printer);

    protected abstract void export(Path p) throws IOException;
}
