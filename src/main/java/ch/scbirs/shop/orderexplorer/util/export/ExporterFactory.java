package ch.scbirs.shop.orderexplorer.util.export;

import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ExporterFactory {
    private String[] header;
    private String ext;

    public ExporterFactory() {

    }

    public ExporterFactory(String ext, String... header) {
        this.ext = ext;
        this.header = header;
    }

    public static List<FileChooser.ExtensionFilter> getSupportedExtensions() {
        return Arrays.asList(
                new FileChooser.ExtensionFilter("Excel File", "*.xls"),
                new FileChooser.ExtensionFilter("TSV File", "*.tsv")
        );
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public void setHeader(String... header) {
        this.header = header;
    }

    public void setPath(Path p) {
        ext = FilenameUtils.getExtension(p.toString());
    }

    public Exporter build() {
        switch (ext) {
            case "xls":
                return new ExcelExporter(header);
            case "tsv":
                return new TsvExporter(header);
            default:
                throw new IllegalStateException("No registered exporter uses extension ." + ext);
        }
    }
}
