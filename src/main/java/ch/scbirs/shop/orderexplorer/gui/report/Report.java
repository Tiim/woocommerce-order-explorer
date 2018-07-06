package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.report.ExporterFactory;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public abstract class Report {
    public abstract PDDocument getPdf();

    public abstract void export(Path path) throws IOException;

    public List<FileChooser.ExtensionFilter> getExtensionFilters() {
        return ExporterFactory.getSupportedExtensions();
    }
}
