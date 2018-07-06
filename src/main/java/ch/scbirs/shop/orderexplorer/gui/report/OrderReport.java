package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.report.Exporter;
import ch.scbirs.shop.orderexplorer.report.ExporterFactory;
import ch.scbirs.shop.orderexplorer.report.PDFExporter;
import ch.scbirs.shop.orderexplorer.report.model.ProductCount;
import ch.scbirs.shop.orderexplorer.report.model.ProductCountFactory;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.Util;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class OrderReport extends Report {

    private static final Logger LOGGER = LogUtil.get();
    private static final String[] HEADER = {"Nr", "Name", "SKU", "Meta", "Price", "Total"};
    private static List<ProductCount> group;

    public OrderReport(Data data) {
        group = new ProductCountFactory(data).build();
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

    private void export(Exporter exp) throws IOException {
        group.forEach(pc -> exp.addData(
                pc.getCount(),
                pc.getProduct().getName(),
                pc.getProduct().getSku(),
                Util.formatMap(pc.getProduct().getMeta()).replace('\n', ','),
                pc.getProduct().getPrice(),
                pc.getProduct().getPrice() * pc.getCount()
                )
        );
    }
}
