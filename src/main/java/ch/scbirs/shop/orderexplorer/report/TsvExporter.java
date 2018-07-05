package ch.scbirs.shop.orderexplorer.report;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class TsvExporter extends Exporter {

    public TsvExporter(String... header) {
        super(header);
    }

    @Override
    public void save(Path p) throws IOException {
        try (Writer w = new OutputStreamWriter(Files.newOutputStream(p))) {
            String string = data.stream()
                    .map(l -> String.join("\t", l))
                    .collect(Collectors.joining("\n"));
            w.write(string);
        }
    }
}
