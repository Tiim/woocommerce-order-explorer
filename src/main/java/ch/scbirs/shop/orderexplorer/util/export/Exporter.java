package ch.scbirs.shop.orderexplorer.util.export;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Exporter {

    protected final List<List<String>> data = new ArrayList<>();
    private final int columns;

    public Exporter(String[] headers) {
        this.columns = headers.length;
        addData((Object[]) headers);
    }

    public void addData(List<String> d) {
        Preconditions.checkArgument(d.size() == columns, "Data size must be equal to columns (%s)", columns);
        data.add(d);
    }

    public void addData(Object... o) {
        Preconditions.checkArgument(o.length == columns, "Data size must be equal to columns (%s)", columns);
        data.add(Arrays.stream(o)
                .map(String::valueOf)
                .collect(Collectors.toList())
        );
    }

    public abstract void save(Path p) throws IOException;
}
