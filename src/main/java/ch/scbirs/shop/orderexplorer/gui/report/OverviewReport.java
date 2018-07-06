package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.report.Exporter;
import ch.scbirs.shop.orderexplorer.report.ExporterFactory;
import ch.scbirs.shop.orderexplorer.report.model.OrderedProduct;
import ch.scbirs.shop.orderexplorer.report.model.OrderedProductFactory;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.Util;
import javafx.print.PageOrientation;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class OverviewReport extends ReportScreen<Data> {

    private static final Logger LOGGER = LogUtil.get();
    private final List<OrderedProduct> productList;


    public OverviewReport(Data data) throws IOException {
        super(data);
        productList = new OrderedProductFactory(data).build();
    }

    @Override
    protected List<FileChooser.ExtensionFilter> getExtensionFilters() {
        return ExporterFactory.getSupportedExtensions();
    }

    @Override
    protected String getFxml() {
        return "overview_report.fxml";
    }

    @Override
    protected Object getNewController(Data data) {
        return new OverviewReportController(productList);
    }

    @Override
    protected void export(Path p) throws IOException {
        ExporterFactory factory = new ExporterFactory();
        factory.setHeader("First Name", "Last Name", "Item", "Quantity", "SKU", "Meta", "Price");
        factory.setPath(p);
        export(p, factory.build());
    }

    @Override
    protected Pane getPrintableNode() {
        return (Pane) getCenter();
    }

    private void export(Path p, Exporter build) throws IOException {
        for (OrderedProduct pr : productList) {
            build.addData(
                    pr.getOrder().getFirstName(),
                    pr.getOrder().getLastName(),
                    pr.getProduct().getName(),
                    pr.getProduct().getQuantity(),
                    pr.getProduct().getSku(),
                    Util.formatMap(pr.getProduct().getMeta()),
                    pr.getProduct().getPrice()
            );
        }
        build.save(p);
    }

    @Override
    protected PageOrientation getPrintOrientation() {
        return PageOrientation.PORTRAIT;
    }
}
