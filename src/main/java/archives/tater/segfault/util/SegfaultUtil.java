package archives.tater.segfault.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class SegfaultUtil {
    private SegfaultUtil() {}

    public static <T, E> Stream<T> foldCompose(Stream<T> initial, Iterable<E> operations, Function<E, UnaryOperator<Stream<T>>> apply) {
        var stream = initial;
        for (var operation : operations)
            stream = apply.apply(operation).apply(stream);
        return stream;
    }

    public static <T> List<T> withAppended(List<T> list, T value) {
        var result = new ArrayList<T>(list.size() + 1);
        result.addAll(list);
        result.add(value);
        return result;
    }
}
