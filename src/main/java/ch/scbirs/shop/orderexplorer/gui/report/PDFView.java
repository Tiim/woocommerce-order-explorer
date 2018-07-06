package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class PDFView extends ImageView {
    private static final Logger LOGGER = LogUtil.get();
    private final SimpleObjectProperty<PDDocument> pdf = new SimpleObjectProperty<>();
    private final SimpleIntegerProperty page = new SimpleIntegerProperty(0);

    public PDFView() {
        setPreserveRatio(true);
        pdf.addListener((observable, oldValue, newValue) -> rerender());
        page.addListener((observable, oldValue, newValue) -> rerender());
    }

    private void rerender() {
        PDFRenderer render = new PDFRenderer(pdf.get());
        try {
            BufferedImage bufimg = render.renderImage(page.get());
            WritableImage img = SwingFXUtils.toFXImage(bufimg, null);
            setImage(img);
        } catch (IOException e) {
            LOGGER.error("Failed to render pdf", e);
        }
    }

    public ObjectProperty<PDDocument> pdfProperty() {
        return pdf;
    }

    public PDDocument getPdf() {
        return pdf.get();
    }

    public void setPdf(PDDocument pdf) {
        this.pdf.set(pdf);
    }

    public IntegerProperty pageProperty() {
        return page;
    }

    public int getPage() {
        return page.get();
    }

    public void setPage(int page) {
        this.page.set(page);
    }

    @Override
    public double minWidth(double height) {
        return 100;
    }

    @Override
    public double prefWidth(double height) {
        Image i = getImage();
        if (i == null) {
            return minWidth(height);
        }
        return i.getWidth();
    }

    @Override
    public double maxWidth(double height) {
        return 16384;
    }

    @Override
    public double minHeight(double width) {
        return 50;
    }

    @Override
    public double prefHeight(double width) {
        Image i = getImage();
        if (i == null) {
            return minHeight(width);
        }
        return i.getWidth();
    }

    @Override
    public double maxHeight(double width) {
        return 16384;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        setFitWidth(width);
        setFitHeight(height);
    }
}
