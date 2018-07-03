package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.Util;
import ch.scbirs.shop.orderexplorer.util.export.Exporter;
import ch.scbirs.shop.orderexplorer.util.export.ExporterFactory;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class OverviewReport extends ReportScreen<Data> {

    private static final Logger LOGGER = LogUtil.get();
    private final List<OrderedProduct> productList;


    public OverviewReport(Data data) throws IOException {
        super(data);
        productList = expand(data);
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

    private List<OrderedProduct> expand(Data data) {
        List<OrderedProduct> out = new ArrayList<>();
        for (Order o : data.getOrders()) {
            for (Product p : o.getProducts()) {
                out.add(new OrderedProduct(o, p));
            }
        }
        return out;
    }

    @Override
    protected void print(Printer printer) {
        Node node = getCenter();
        PageLayout pageLayout
                = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        double scaleX
                = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY
                = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        Scale scale = new Scale(scaleX, scaleY);
        node.getTransforms().add(scale);

        if (job != null && job.showPrintDialog(node.getScene().getWindow())) {
            boolean success = job.printPage(pageLayout, node);
            if (success) {
                job.endJob();
            }
        }
        node.getTransforms().remove(scale);
    }

    @Override
    protected void export(Path p) throws IOException {
        ExporterFactory factory = new ExporterFactory();
        factory.setHeader("First Name", "Last Name", "Item", "Quantity", "SKU", "Meta", "Price");
        factory.setPath(p);
        export(p, factory.build());
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
}
