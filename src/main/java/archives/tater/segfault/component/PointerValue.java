package archives.tater.segfault.component;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;

public record PointerValue(BlockPos pos) implements TooltipProvider {
    public static final PointerValue NULL = new PointerValue(BlockPos.ZERO);

    public static final Codec<PointerValue> CODEC = BlockPos.CODEC.xmap(PointerValue::new, PointerValue::pos);

    public static final StreamCodec<ByteBuf, PointerValue> STREAM_CODEC = BlockPos.STREAM_CODEC.map(PointerValue::new, PointerValue::pos);

    public boolean isNull() {
        return pos.equals(BlockPos.ZERO);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept((isNull()
                ? Component.translatable("item.segfault.pointer.null")
                : Component.translatable("item.segfault.pointer.value", pos.getX(), pos.getY(), pos.getZ())
        ).withStyle(ChatFormatting.GOLD));
    }
}
