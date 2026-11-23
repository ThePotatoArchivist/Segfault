package archives.tater.segfault.component;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;

// TODO
public record StructTemplate(Identifier structureId) implements TooltipProvider {
    public static final Codec<StructTemplate> CODEC = Identifier.CODEC.xmap(StructTemplate::new, StructTemplate::structureId);
    public static final StreamCodec<ByteBuf, StructTemplate> STREAM_CODEC = Identifier.STREAM_CODEC.map(StructTemplate::new, StructTemplate::structureId);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.literal(structureId.toString())
                .withStyle(ChatFormatting.GRAY));
    }
}
