package ch.scbirs.shop.orderexplorer.backup;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BackupProvider {
    private static final Logger LOGGER = LogUtil.get();
    private static final int KEEP = 10;
    private static final String FOLDER = "backups";
    private static final String FORMAT = "backup-%s.json";
    private static final Pattern REGEX = Pattern.compile("backup-(.+)\\.json");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HH-mm-ss");

    private static ObservableList<String> backups;
    private static Path root;

    public static void setRoot(Path root) {
        if (BackupProvider.root != null) {
            throw new IllegalStateException("Root path already set");
        }
        BackupProvider.root = root;
        try {
            Files.createDirectories(root.resolve(FOLDER));
        } catch (IOException e) {
            LOGGER.error("Can't create backup folder");
        }
    }


    public static void nextBackup(Data data) throws IOException {
        if (data == null) {
            return;
        }
        Path folder = root.resolve(FOLDER);

        Files.createDirectories(folder);

        newBackup(data, folder);

        cleanupOldBackups(folder);
    }

    public static Data loadBackup(String name) throws IOException {
        return Data.fromJsonFile(root.resolve(FOLDER).resolve(name));
    }

    private static void cleanupOldBackups(Path folder) throws IOException {
        List<String> backups = listBackups(folder);
        backups.sort(Comparator.comparing(BackupProvider::getDate));

        Queue<String> quque = new ArrayDeque<>(backups);

        while (quque.size() > KEEP) {
            String oldest = quque.poll();
            LOGGER.info("Deleting backup " + oldest);

            Files.delete(folder.resolve(oldest));
            backups.remove(oldest);
        }
    }

    private static void newBackup(Data data, Path folder) throws IOException {
        String fileName = String.format(FORMAT, LocalDateTime.now().format(DATE_FORMATTER));
        Path file = folder.resolve(fileName);
        LOGGER.info("New Backup " + file);
        Data.toJsonFile(file, data);
        backups.add(fileName);
    }

    private static LocalDateTime getDate(String filename) {
        Matcher m = REGEX.matcher(filename);
        if (!m.matches()) {
            LOGGER.warn("Can't parse backup " + filename);
            return LocalDateTime.MIN;
        }
        try {
            return LocalDateTime.parse(m.group(1), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            LOGGER.warn("Error parsing date of " + filename);
            return LocalDateTime.MIN;
        }
    }

    private static List<String> listBackups(Path folder) throws IOException {
        return Files.list(folder)
                .map(p -> FilenameUtils.getName(p.toString()))
                .collect(Collectors.toList());
    }

    public static ObservableList<String> backups() {
        if (backups == null) {
            try {
                backups = FXCollections.observableList(listBackups(root.resolve(FOLDER)));
            } catch (IOException e) {
                LOGGER.error("Can't list backups", e);
                backups = FXCollections.observableArrayList();
            }
        }
        return backups;
    }

}
