package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.report.Exporter;
import ch.scbirs.shop.orderexplorer.report.ExporterFactory;
import ch.scbirs.shop.orderexplorer.report.PDFExporter;
import ch.scbirs.shop.orderexplorer.report.model.OrderedProduct;
import ch.scbirs.shop.orderexplorer.report.model.OrderedProductFactory;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.Util;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class OverviewReport extends Report {

    private static final Logger LOGGER = LogUtil.get();
    private static final String[] HEADER = {"First Name", "Last Name", "Item", "Quantity", "SKU", "Meta", "Price"};
    private final List<OrderedProduct> productList;


    public OverviewReport(Data data) throws IOException {
        productList = new OrderedProductFactory(data).build();
    }

    @Override
    public List<FileChooser.ExtensionFilter> getExtensionFilters() {
        return ExporterFactory.getSupportedExtensions();
    }

    @Override
    public PDDocument getPdf() {
        try {
            PDFExporter pdfExporter = new PDFExporter(HEADER);
            export(pdfExporter);
            return pdfExporter.generatePDDocument();
        } catch (IOException e) {
            LOGGER.warn("Can't generate pdf document", e);
        }
        return null;
    }

    @Override
    public void export(Path p) throws IOException {
        ExporterFactory factory = new ExporterFactory();
        factory.setHeader(HEADER);
        factory.setPath(p);
        Exporter exporter = factory.build();
        export(exporter);
        exporter.save(p);
    }

    private void export(Exporter exporter) throws IOException {
        for (OrderedProduct pr : productList) {
            exporter.addData(
                    pr.getOrder().getFirstName(),
                    pr.getOrder().getLastName(),
                    pr.getProduct().getName(),
                    pr.getProduct().getQuantity(),
                    pr.getProduct().getSku(),
                    Util.formatMap(pr.getProduct().getMeta()),
                    pr.getProduct().getPrice()
            );
        }
    }
}
