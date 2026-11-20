package archives.tater.segfault.allocation;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

public record DisplayRegion(GridRegion region, boolean free) {
    public static final StreamCodec<ByteBuf, DisplayRegion> STREAM_CODEC = StreamCodec.composite(
            GridRegion.STREAM_CODEC, DisplayRegion::region,
            ByteBufCodecs.BOOL, DisplayRegion::free,
            DisplayRegion::new
    );
}
