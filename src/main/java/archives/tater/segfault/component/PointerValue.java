package archives.tater.segfault.component;

import archives.tater.segfault.registry.SegfaultModAttachments;
import archives.tater.segfault.registry.SegfaultModDimensions;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record PointerValue(BlockPos pos) implements TooltipProvider {
    public static final PointerValue NULL = new PointerValue(BlockPos.ZERO);

    public static final Codec<PointerValue> CODEC = BlockPos.CODEC.xmap(PointerValue::new, PointerValue::pos);
    public static final StreamCodec<ByteBuf, PointerValue> STREAM_CODEC = BlockPos.STREAM_CODEC.map(PointerValue::new, PointerValue::pos);

    public static final TeleportTransition.PostTeleportTransition SET_EXITED = PointerValue::setExited;

    public boolean isNull() {
        return pos.equals(BlockPos.ZERO);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("item.segfault.pointer.target", getDescription()).withStyle(ChatFormatting.GOLD));
    }

    public Component getDescription() {
        return isNull()
                ? Component.translatable("item.segfault.pointer.null")
                : Component.translatable("item.segfault.pointer.value", pos.getX(), pos.getY(), pos.getZ());
    }

    public static @Nullable TeleportTransition getDestination(ServerLevel level, @Nullable PointerValue pointer, Entity entity) {
        if (pointer == null) return getExitDestination(level, entity);

        var memory = level.getServer().getLevel(SegfaultModDimensions.MEMORY);
        if (memory == null) return null;

        if (entity.level().dimension() != SegfaultModDimensions.MEMORY)
            entity.setAttached(SegfaultModAttachments.ENTRY_POS, GlobalPos.of(entity.level().dimension(), entity.blockPosition()));
        return new TeleportTransition(memory, Vec3.atBottomCenterOf(pointer.pos()), Vec3.ZERO, 0, 0, Relative.ROTATION, TeleportTransition.DO_NOTHING);
    }

    public static @Nullable TeleportTransition getExitDestination(ServerLevel level, Entity entity) {
        var entryPos = entity.removeAttached(SegfaultModAttachments.ENTRY_POS);
        if (entryPos == null || level.dimension() != SegfaultModDimensions.MEMORY) return null;

        var newLevel = level.getServer().getLevel(entryPos.dimension());
        if (newLevel == null) return null;

        return new TeleportTransition(newLevel, Vec3.atBottomCenterOf(entryPos.pos()), Vec3.ZERO, 0, 0, Relative.ROTATION, SET_EXITED);
    }

    private static void setExited(final Entity entity) {
        entity.setAttached(SegfaultModAttachments.EXITED_MEMORY, Unit.INSTANCE);
    }
}
