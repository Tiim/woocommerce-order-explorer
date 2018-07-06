package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.gui.ExceptionAlert;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private Parent root;
    @FXML
    private PDFView pdfView;


    public ReportScreen(Report report) throws IOException {
        this.report = report;

        FXMLLoader loader = new FXMLLoader(ReportScreen.class.getResource("report_toolbar.fxml"));
        loader.setController(this);
        loader.load();
    }

    public void show() {

        pdfView.setPdf(report.getPdf());

        stage = new Stage();
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setResizable(true);

        stage.show();
    }

    @FXML
    private void print() {
        PDDocument document = pdfView.getPdf();
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        if (job.printDialog()) {
            job.pageDialog(job.defaultPage());
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
}
