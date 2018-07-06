package ch.scbirs.shop.orderexplorer.report;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class PDFExporter extends Exporter {
    private static final Logger LOGGER = LogUtil.get();

    public PDFExporter(String[] header) {
        super(header);
    }

    @Override
    public void save(Path p) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage myPage = new PDPage();

            float margin = 25;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);
            float bottomMargin = 50;
            // y position is your coordinate of top left corner of the table
            float yPosition = 50;

            double[] headerSize = getStringWidths(data, PDType1Font.HELVETICA);

            BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin,
                    doc, myPage, true, true);


            List<String> header = data.get(0);
            Row<PDPage> headerRow = table.createRow(15f);

            for (int i = 0; i < header.size(); i++) {
                String v = header.get(i);
                Cell<PDPage> cell = headerRow.createCell((float) (headerSize[i] * 100), v);
            }
            table.addHeaderRow(headerRow);


            for (int i = 1; i < data.size(); i++) {
                List<String> d = data.get(i);
                Row<PDPage> row = table.createRow(15);
                row(d, row);
            }

            table.draw();


            doc.addPage(myPage);
            doc.save(p.toFile());
        }
    }

    private double[] getStringWidths(List<List<String>> table, PDFont font) throws IOException {

        final double maxRel = 0.4;
        final double minRel = 0.1;

        double[] maxLength = new double[table.get(0).size()];

        for (int i = 0; i < table.size(); i++) {
            List<String> row = table.get(i);
            for (int j = 0; j < row.size(); j++) {
                float width = font.getStringWidth(row.get(j));
                maxLength[j] = Math.max(maxLength[j], width + 1000);
            }
        }

        LOGGER.trace("Lengths: " + Arrays.toString(maxLength));

        double sum = Arrays.stream(maxLength).sum();
        double cols = maxLength.length;

        double[] rel = Arrays.stream(maxLength)
                .map(l -> l / sum)
                .map(l -> Math.min(Math.max(minRel, l), maxRel))
                .toArray();

        LOGGER.trace("Relations: " + Arrays.toString(rel));

        double newsum = Arrays.stream(rel).sum();
        return Arrays.stream(rel).map(l -> l / newsum).toArray();
    }

    private void row(List<String> vals, Row row) {
        for (String val : vals) {
            row.createCell(val);
        }
    }
}
