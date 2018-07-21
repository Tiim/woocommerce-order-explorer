package ch.scbirs.shop.orderexplorer.gui.util;

import ch.scbirs.shop.orderexplorer.util.ExceptionUtil;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.VersionUtil;
import com.google.common.collect.ImmutableMap;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;

public class ExceptionAlert extends Alert {

    private static final Logger LOGGER = LogUtil.get();
    private static HostServices hostServices;

    private final String stacktrace;
    private final Throwable throwable;

    public ExceptionAlert(Throwable t) {
        super(AlertType.ERROR);
        throwable = t;
        setTitle("Exception!");
        setHeaderText("An unexpected error occured");
        setContentText("Error Message:\n" + t.getMessage());


        stacktrace = ExceptionUtils.getStackTrace(t);

        TextArea textArea = new TextArea(stacktrace);
        textArea.setEditable(false);
        textArea.setWrapText(true);


        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        Button reportBtn = new Button("Report");
        reportBtn.setOnAction(event -> sendReport());

        Button copyBtn = new Button("Copy");
        copyBtn.setOnAction(event -> copyToClipboard());

        HBox buttons = new HBox(reportBtn, copyBtn);
        buttons.setSpacing(5);

        VBox.setVgrow(buttons, Priority.NEVER);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        VBox expContent = new VBox(buttons, textArea);
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.setSpacing(5);

        getDialogPane().setExpandableContent(expContent);
    }

    public static void setHostServices(HostServices hostServices) {
        ExceptionAlert.hostServices = hostServices;
    }

    private void copyToClipboard() {
        LOGGER.info("Copying stacktrace to clipboard");
        Clipboard.getSystemClipboard().setContent(ImmutableMap.of(DataFormat.PLAIN_TEXT, stacktrace));
    }

    private void sendReport() {
        LOGGER.info("Opening bug report url");
        hostServices.showDocument(ExceptionUtil.generateBugUrl(throwable, VersionUtil.getFullVersion()));
    }

    public static void doTry(RunnableWithException toTry) {
        try {
            toTry.run();
        } catch (Exception e) {
            LOGGER.warn("Exception Dialog showing to the user: ", e);
            Platform.runLater(() -> {
                Alert error = new ExceptionAlert(e);
                error.show();
            });
        }
    }

    public interface RunnableWithException {
        void run() throws Exception;
    }
}
