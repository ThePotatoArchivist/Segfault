package archives.tater.segfault.component;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;

// TODO
public record StructTemplate(Vec3i dimensions) implements TooltipProvider {
    public static final Codec<StructTemplate> CODEC = Vec3i.CODEC.xmap(StructTemplate::new, StructTemplate::dimensions);
    public static final StreamCodec<ByteBuf, StructTemplate> STREAM_CODEC = Vec3i.STREAM_CODEC.map(StructTemplate::new, StructTemplate::dimensions);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("item.segfault.struct_template.dimensions", dimensions.getX(), dimensions.getY(), dimensions.getZ())
                .withStyle(ChatFormatting.GRAY));
    }
}
