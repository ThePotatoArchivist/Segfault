package archives.tater.segfault.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.ChunkPos;

import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;
import java.util.function.Function;

public record PointerValue(ChunkPos pos, int offsetX, int offsetZ) implements TooltipProvider {

    public static final Codec<PointerValue> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChunkPos.CODEC.fieldOf("pos").forGetter(PointerValue::pos),
            Codec.intRange(0, 15).optionalFieldOf("offset_x", 0).forGetter(PointerValue::offsetX),
            Codec.intRange(0, 15).optionalFieldOf("offset_z", 0).forGetter(PointerValue::offsetZ)
    ).apply(instance, PointerValue::new));

    public static final Codec<PointerValue> CODEC = Codec.either(FULL_CODEC, ChunkPos.CODEC).xmap(
            either -> either.map(Function.identity(), PointerValue::new),
            value -> value.hasOffset() ? Either.left(value) : Either.right(value.pos)
    );

    public static final StreamCodec<ByteBuf, PointerValue> STREAM_CODEC = StreamCodec.composite(
            ChunkPos.STREAM_CODEC, PointerValue::pos,
            ByteBufCodecs.VAR_INT, PointerValue::offsetX,
            ByteBufCodecs.VAR_INT, PointerValue::offsetZ,
            PointerValue::new
    );

    public PointerValue(ChunkPos pos) {
        this(pos, 0, 0);
    }

    public boolean hasOffset() {
        return offsetX != 0 || offsetZ != 0;
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("item.segfault.pointer.value", pos.getBlockX(offsetX), pos.getBlockZ(offsetZ))
                .withStyle(ChatFormatting.GOLD));
    }
}
