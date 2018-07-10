package ch.scbirs.shop.orderexplorer.util;

import javafx.beans.WeakListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class BindingUtil {

    public static <E, F> void mapContent(ObservableList<F> mapped, ObservableList<? extends E> source,
                                         Function<? super E, ? extends F> mapper, Consumer<F> addedCallback, Consumer<F> removedCallback) {
        map(mapped, source, mapper, addedCallback, removedCallback);
    }

    private static <E, F> Object map(ObservableList<F> mapped, ObservableList<? extends E> source,
                                     Function<? super E, ? extends F> mapper, Consumer<F> addedCallback,
                                     Consumer<F> removedCallback) {
        final ListContentMapping<E, F> contentMapping = new ListContentMapping<>(mapped, mapper, addedCallback, removedCallback);
        mapped.setAll(source.stream()
                .map(o -> mapper.apply(o))
                .peek(addedCallback::accept)
                .collect(toList())
        );
        source.removeListener(contentMapping);
        source.addListener(contentMapping);
        return contentMapping;
    }

    private static class ListContentMapping<E, F> implements ListChangeListener<E>, WeakListener {
        private final WeakReference<List<F>> mappedRef;
        private final Function<? super E, ? extends F> mapper;
        private final Consumer<F> addedCallback;
        private final Consumer<F> removedCallback;

        public ListContentMapping(List<F> mapped, Function<? super E, ? extends F> mapper,
                                  Consumer<F> addedCallback, Consumer<F> removedCallback) {
            this.mappedRef = new WeakReference<List<F>>(mapped);
            this.mapper = mapper;
            this.addedCallback = addedCallback;
            this.removedCallback = removedCallback;
        }

        @Override
        public void onChanged(Change<? extends E> change) {
            final List<F> mapped = mappedRef.get();
            if (mapped == null) {
                change.getList().removeListener(this);
            } else {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        mapped.subList(change.getFrom(), change.getTo()).clear();
                        mapped.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo())
                                .stream().map(mapper::apply).collect(toList()));
                    } else {
                        if (change.wasRemoved()) {
                            List<F> sublist = mapped.subList(change.getFrom(), change.getFrom() + change.getRemovedSize());
                            sublist.forEach(removedCallback);
                            sublist.clear();
                        }
                        if (change.wasAdded()) {
                            mapped.addAll(change.getFrom(), change.getAddedSubList()
                                    .stream().map(mapper::apply).peek(addedCallback).collect(toList()));
                        }
                    }
                }
            }
        }

        @Override
        public boolean wasGarbageCollected() {
            return mappedRef.get() == null;
        }

        @Override
        public int hashCode() {
            final List<F> list = mappedRef.get();
            return (list == null) ? 0 : list.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            final List<F> mapped1 = mappedRef.get();
            if (mapped1 == null) {
                return false;
            }

            if (obj instanceof ListContentMapping) {
                final ListContentMapping<?, ?> other = (ListContentMapping<?, ?>) obj;
                final List<?> mapped2 = other.mappedRef.get();
                return mapped1 == mapped2;
            }
            return false;
        }
    }
}