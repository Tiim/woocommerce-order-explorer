package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.report.Exporter;
import ch.scbirs.shop.orderexplorer.report.ExporterFactory;
import ch.scbirs.shop.orderexplorer.report.model.ProductCount;
import ch.scbirs.shop.orderexplorer.report.model.ProductCountFactory;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.Util;
import javafx.print.PageOrientation;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class OrderReport extends ReportScreen<Data> {
    private static final Logger LOGGER = LogUtil.get();

    private final List<ProductCount> group;


    public OrderReport(Data data) throws IOException {
        super(data);
        group = new ProductCountFactory(data).build();
    }

    @Override
    protected List<FileChooser.ExtensionFilter> getExtensionFilters() {
        return ExporterFactory.getSupportedExtensions();
    }

    @Override
    protected void export(Path p) throws IOException {
        ExporterFactory factory = new ExporterFactory();
        factory.setHeader("Nr", "Name", "SKU", "Meta", "Price", "Total");
        factory.setPath(p);
        export(p, factory.build());
    }

    @Override
    protected Pane getPrintableNode() {
        return (Pane) getCenter();
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

    @Override
    protected String getFxml() {
        return "order_report.fxml";
    }

    @Override
    protected Object getNewController(Data data) {
        return new OrderReportController(group);
    }

    @Override
    protected PageOrientation getPrintOrientation() {
        return PageOrientation.PORTRAIT;
    }
}
