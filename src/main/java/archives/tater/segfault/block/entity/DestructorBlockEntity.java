package archives.tater.segfault.block.entity;

import archives.tater.segfault.AllocationManager;
import archives.tater.segfault.component.PointerValue;
import archives.tater.segfault.menu.DestructorMenu;
import archives.tater.segfault.registry.SegfaultModBlockEntities;
import archives.tater.segfault.registry.SegfaultModComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

public class DestructorBlockEntity extends SimpleContainerBlockEntity implements WorldlyContainer {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_RESULT = 1;
    private static final int[] SLOTS_FOR_UP_SIDES = {SLOT_INPUT};
    private static final int[] SLOTS_FOR_DOWN = {SLOT_RESULT};

    public static final Component DEFAULT_NAME = Component.translatable("container.segfault.destructor");

    public DestructorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(SegfaultModBlockEntities.DESTRUCTOR, worldPosition, blockState);
    }

    public ItemStack getInput() {
        return getItem(SLOT_INPUT);
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
        return new DestructorMenu(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return slot == SLOT_INPUT && itemStack.has(SegfaultModComponents.POINTER);
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return direction == Direction.DOWN
                ? SLOTS_FOR_DOWN
                : SLOTS_FOR_UP_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        return canPlaceItem(slot, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return true;
    }

    public void tryDestruct(MinecraftServer server) {
        var input = getInput().copyWithCount(1);
        if (input.isEmpty()) return;

        var pointer = input.set(SegfaultModComponents.POINTER, PointerValue.NULL);
        if (pointer == null) return;
        AllocationManager.get(server).destruct(pointer);

        var result = getResult();

        if (result.isEmpty()) {
            setResult(input);
        } else {
             if (!ItemStack.isSameItemSameComponents(result, input)) return;
             result.setCount(result.count() + 1);
        }

        getInput().shrink(1);
        setChanged();
    }

}
