package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.gui.util.ExceptionAlert;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

public class ReportScreen {

    private static final Logger LOGGER = LogUtil.get();
    private final Report report;
    private Stage stage;

    @FXML
    private BorderPane root;
    @FXML
    private PDFView pdfView;
    @FXML
    private Label page;


    public ReportScreen(Report report) throws IOException {
        this.report = report;

        FXMLLoader loader = new FXMLLoader(ReportScreen.class.getResource("report_window.fxml"));
        loader.setController(this);
        loader.load();
    }

    @FXML
    private void initialize() {
        page.textProperty().bind(pdfView.pageProperty().asString());
    }

    public void show() {

        pdfView.setPdf(report.getPdf());

        stage = new Stage();
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setResizable(false);
        stage.sizeToScene();

        stage.show();
    }

    @FXML
    private void print() {
        PDDocument document = pdfView.getPdf();
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        if (job.printDialog()) {
            ExceptionAlert.doTry(() -> job.print());
        }
    }

    @FXML
    private void exportImpl() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select where to save the report");
        chooser.getExtensionFilters().addAll(report.getExtensionFilters());
        File selectedFile = chooser.showSaveDialog(stage);
        if (selectedFile != null) {
            ExceptionAlert.doTry(() -> report.export(selectedFile.toPath()));
        }
    }

    @FXML
    private void pageNext() {
        pdfView.setPage(pdfView.getPage() + 1);
    }


    @FXML
    private void pagePrev() {
        pdfView.setPage(pdfView.getPage() - 1);
    }
}
