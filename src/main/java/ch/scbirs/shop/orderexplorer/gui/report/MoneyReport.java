package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.report.Exporter;
import ch.scbirs.shop.orderexplorer.report.ExporterFactory;
import ch.scbirs.shop.orderexplorer.report.PDFExporter;
import ch.scbirs.shop.orderexplorer.report.model.OrderSummary;
import ch.scbirs.shop.orderexplorer.report.model.OrderSummaryFactory;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class MoneyReport extends Report {

    private static final Logger LOGGER = LogUtil.get();
    private static final String[] HEADER = {"ID", "First Name", "Last Name", "E-Mail", "Total"};

    private final List<OrderSummary> summary;

    public MoneyReport(Data data) {

        summary = new OrderSummaryFactory(data).build();
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

    private void export(Exporter exp) {
        summary.forEach(s -> exp.addData(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getTotal()
                )
        );
    }

    @Override
    public String getName(ResourceBundle resources) {
        return resources.getString("app.report.money.Name");
    }
}
