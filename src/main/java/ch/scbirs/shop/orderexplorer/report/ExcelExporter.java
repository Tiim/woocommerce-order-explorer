package ch.scbirs.shop.orderexplorer.report;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExcelExporter extends Exporter {

    public ExcelExporter(String... headers) {
        super(headers);
    }

    @Override
    public void save(Path p) throws IOException {
        try (Workbook workbook = new HSSFWorkbook();
             OutputStream os = Files.newOutputStream(p)) {
            Sheet sheet = workbook.createSheet("Report");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            CellStyle normalStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            row(data.get(0), sheet.createRow(0), headerStyle);

            for (int i = 1; i < data.size(); i++) {
                row(data.get(i), sheet.createRow(i), normalStyle);
            }

            workbook.write(os);
        }
    }

    private void row(List<String> vals, Row row, CellStyle style) {
        for (int i = 0; i < vals.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(vals.get(i));
            cell.setCellStyle(style);
        }
    }
}
