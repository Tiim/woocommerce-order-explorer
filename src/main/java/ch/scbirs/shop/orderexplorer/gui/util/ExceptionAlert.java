package ch.scbirs.shop.orderexplorer.gui.util;

import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionAlert extends Alert {

    private static final Logger LOGGER = LogUtil.get();

    public ExceptionAlert(Throwable t) {
        super(AlertType.ERROR);
        setTitle("Exception!");
        setHeaderText("An unexpected error occured");
        setContentText(t.getMessage());


        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);


        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        getDialogPane().setExpandableContent(expContent);
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
