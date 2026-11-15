package archives.tater.segfault.util;

import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class LazyDerived<T, U> implements Supplier<U> {
    private final Supplier<T> source;
    private final Function<T, U> calculate;

    private @Nullable T lastKey;
    private int lastKeyHash = 0;

    private @Nullable U value;

    public LazyDerived(Supplier<T> source, Function<T, U> calculate) {
        this.source = source;
        this.calculate = calculate;
    }

    @Override
    public U get() {
        var key = source.get();
        var hash = key.hashCode();
        if (value != null && hash == lastKeyHash && key.equals(lastKey))
            return value;

        lastKey = key;
        lastKeyHash = hash;
        return value = calculate.apply(key);
    }
}
