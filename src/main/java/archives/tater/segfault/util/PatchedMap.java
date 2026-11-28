package archives.tater.segfault.util;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import com.mojang.serialization.Codec;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public sealed interface PatchedMap<K, V> {
    PatchedMap<?, ?> EMPTY = new Base<>(Map.of());

    @SuppressWarnings("unchecked")
    static <K, V> PatchedMap<K, V> empty() {
        return (PatchedMap<K, V>) EMPTY;
    }

    static <K, V> void put(AttachmentTarget target, AttachmentType<PatchedMap<K, V>> type, K key, V value) {
        target.setAttached(type, new Patched<>(Map.of(key, value), target.getAttachedOrElse(type, empty())));
    }

    static <K, V> @Nullable V remove(AttachmentTarget target, AttachmentType<PatchedMap<K, V>> type, K key) {
        var current = target.getAttached(type);
        if (current == null || current instanceof Base<K, V>(var values) && !values.containsKey(key))
            return null;

        var newPatch = new HashMap<K, V>();
        while (true) switch (current) {
            case Base(var values) -> {
                values.forEach(newPatch::putIfAbsent);

                newPatch.remove(key);
                target.setAttached(type, new Base<>(newPatch));

                return values.get(key);
            }
            case Patched(var patch, var base) -> {
                patch.forEach(newPatch::putIfAbsent);

                var value = patch.get(key);
                if (value != null) {
                    newPatch.remove(key);
                    target.setAttached(type, new Patched<>(newPatch, base));
                    return value;
                }

                current = base;
            }
        }
    }

    static <K, V> Map<K, V> flatten(PatchedMap<K, V> map) {
        var result = new HashMap<K, V>();
        var current = map;
        while (true) switch (current) {
            case Base(var values) -> {
                values.forEach(result::putIfAbsent);
                return result;
            }
            case Patched(var patch, var base) -> {
                patch.forEach(result::putIfAbsent);
                current = base;
            }
        }
    }

    static <K, V> Codec<PatchedMap<K, V>> codec(Codec<Map<K, V>> mapCodec) {
        return mapCodec.xmap(Base::new, PatchedMap::flatten);
    }

    record Base<K, V>(@Unmodifiable Map<K, V> values) implements PatchedMap<K, V> {}

    record Patched<K, V>(@Unmodifiable Map<K, V> patch, PatchedMap<K, V> base) implements PatchedMap<K, V> {}
}
