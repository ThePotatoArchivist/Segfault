package archives.tater.segfault.block.entity;

import archives.tater.segfault.allocation.AllocationManager;
import archives.tater.segfault.menu.ConstructorMenu;
import archives.tater.segfault.registry.SegfaultModBlockEntities;
import archives.tater.segfault.registry.SegfaultModComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

public class ConstructorBlockEntity extends SimpleContainerBlockEntity implements WorldlyContainer {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_TEMPLATE = 1;
    public static final int SLOT_RESULT = 2;
    private static final int[] SLOTS_FOR_UP = {SLOT_INPUT};
    private static final int[] SLOTS_FOR_SIDE = {SLOT_TEMPLATE};
    private static final int[] SLOTS_FOR_DOWN = {SLOT_RESULT};

    public static final Component DEFAULT_NAME = Component.translatable("container.segfault.constructor");

    public ConstructorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(SegfaultModBlockEntities.CONSTRUCTOR, worldPosition, blockState);
    }

    public ItemStack getInput() {
        return getItem(SLOT_INPUT);
    }

    public ItemStack getTemplate() {
        return getItem(SLOT_TEMPLATE);
    }

    public ItemStack getResult() {
        return getItem(SLOT_RESULT);
    }

    public void setResult(ItemStack stack) {
        setItem(SLOT_RESULT, stack);
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ConstructorMenu(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return switch (slot) {
            case SLOT_INPUT -> itemStack.has(SegfaultModComponents.POINTER);
            case SLOT_TEMPLATE -> itemStack.has(SegfaultModComponents.STRUCT_TEMPLATE);
            default -> false;
        };
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return direction == Direction.UP ? SLOTS_FOR_UP :
                direction == Direction.DOWN ? SLOTS_FOR_DOWN :
                SLOTS_FOR_SIDE;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        return canPlaceItem(slot, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return true;
    }

    public void tryAllocate(MinecraftServer server) {
        if (!getResult().isEmpty()) return;

        var template = getTemplate().get(SegfaultModComponents.STRUCT_TEMPLATE);
        if (template == null) return;

        var input = getInput();
        if (input.isEmpty()) return;

        var pointer = AllocationManager.get(server).construct(server, template);
        if (pointer.isNull()) return;

        getLevel().playSound(null, getBlockPos(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS); // TODO custom sound event

        var result = input.split(1);
        result.set(SegfaultModComponents.POINTER, pointer);
        setResult(result);
    }

}
