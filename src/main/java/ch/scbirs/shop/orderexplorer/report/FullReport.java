package ch.scbirs.shop.orderexplorer.report;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.products.ProductVariation;
import ch.scbirs.shop.orderexplorer.report.model.OrderedProduct;
import ch.scbirs.shop.orderexplorer.report.model.OrderedProductFactory;
import ch.scbirs.shop.orderexplorer.report.model.ProductCount;
import ch.scbirs.shop.orderexplorer.report.model.ProductCountFactory;
import ch.scbirs.shop.orderexplorer.util.Util;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FullReport {

    private final Data data;
    private CellStyle headerStyle;
    private CellStyle normalStyle;

    public FullReport(Data data) {

        this.data = data;
    }

    public void save(Path p) throws IOException {
        try (Workbook wb = new HSSFWorkbook();
             OutputStream os = Files.newOutputStream(p)) {
            makeStyles(wb);

            generateOrderReport(wb);
            generateOverviewReport(wb);
            generateProductsReport(wb);

            wb.write(os);
        }
    }

    private void generateProductsReport(Workbook wb) {
        Sheet sheet = wb.createSheet("All Products");

        int cols = addHeader(sheet, "Name", "ProductID", "Price", "Url");
        int rowNr = 1;
        List<ProductVariation> var = data.getProductVariations();

        for (ProductVariation v : var) {
            int c = 0;
            Row r = sheet.createRow(rowNr++);

            c = makeCell(c, r, v.getName());
            c = makeCell(c, r, v.getSku());
            c = makeCell(c, r, v.getPrice());
            makeCell(c, r, v.getPermalink());
        }
        autosize(sheet, cols);
    }

    private void generateOverviewReport(Workbook wb) {
        Sheet sheet = wb.createSheet("Ordered Product Summary");

        int cols = addHeader(sheet, "Amount", "Name", "SKU", "Meta", "Price", "Total");
        int rowNr = 1;
        List<ProductCount> pc = new ProductCountFactory(data).build();

        for (ProductCount p : pc) {
            int c = 0;
            Row r = sheet.createRow(rowNr++);

            c = makeCell(c, r, p.getCount());
            c = makeCell(c, r, p.getProduct().getName());
            c = makeCell(c, r, p.getProduct().getSku());
            c = makeCell(c, r, Util.formatMap(p.getProduct().getMeta()));
            double price = p.getProduct().getPrice();
            c = makeCell(c, r, price);
            makeCell(c, r, price * p.getCount());
        }
        autosize(sheet, cols);
    }


    private void generateOrderReport(Workbook wb) {
        Sheet sheet = wb.createSheet("All Orders");


        int cols = addHeader(sheet, "Id", "Order Id", "First Name", "Last Name", "Product Name",
                "SKU", "Meta", "Amount", "Price", "Total", "Status");

        int rowNr = 1;
        List<OrderedProduct> op = new OrderedProductFactory(data).build();
        for (OrderedProduct p : op) {
            int c = 0;
            Row r = sheet.createRow(rowNr++);

            c = makeCell(c, r, p.getProduct().getId());
            c = makeCell(c, r, p.getOrder().getId());
            c = makeCell(c, r, p.getOrder().getFirstName());
            c = makeCell(c, r, p.getOrder().getLastName());
            c = makeCell(c, r, p.getProduct().getName());
            c = makeCell(c, r, p.getProduct().getSku());
            c = makeCell(c, r, Util.formatMap(p.getProduct().getMeta()));
            int quantity = p.getProduct().getQuantity();
            c = makeCell(c, r, quantity);
            c = makeCell(c, r, p.getProduct().getPrice());
            c = makeCell(c, r, p.getProduct().getPrice() * quantity);
            makeCell(c, r, data.getUserData().getProductData(
                    p.getProduct()
            ).getStatus().toString().toLowerCase());
        }
        autosize(sheet, cols);
    }

    private int addHeader(Sheet sheet, String... header) {
        Row r = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            String s = header[i];
            Cell cell = r.createCell(i);
            cell.setCellValue(s);
            cell.setCellStyle(headerStyle);
        }
        return header.length;
    }

    private int makeCell(int i, Row r, String value) {
        Cell c = r.createCell(i++);
        c.setCellValue(value);
        return i;
    }

    private int makeCell(int i, Row r, double value) {
        Cell c = r.createCell(i++);
        c.setCellValue(value);
        return i;
    }

    private void autosize(Sheet s, int cols) {
        for (int i = 0; i < cols; i++) {
            s.autoSizeColumn(i);
        }
    }

    private void makeStyles(Workbook wb) {
        Font bold = wb.createFont();
        bold.setBold(true);

        headerStyle = wb.createCellStyle();
        headerStyle.setFont(bold);
        normalStyle = wb.createCellStyle();

    }
}
