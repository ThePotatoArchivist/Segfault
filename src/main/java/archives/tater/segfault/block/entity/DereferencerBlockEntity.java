package archives.tater.segfault.block.entity;

import archives.tater.segfault.block.DereferencerBlock;
import archives.tater.segfault.menu.DereferencerMenu;
import archives.tater.segfault.registry.SegfaultModBlockEntities;
import archives.tater.segfault.registry.SegfaultModComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class DereferencerBlockEntity extends BaseSingleItemContainerBlockEntity {

    public static final Component DEFAULT_NAME = Component.translatable("container.segfault.dereference");

    public DereferencerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(SegfaultModBlockEntities.DEREFERENCER, worldPosition, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return itemStack.has(SegfaultModComponents.POINTER);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new DereferencerMenu(containerId, inventory, this);
    }

    private void updateState() {
        if (!getBlockState().getValue(DereferencerBlock.POWERED)) return;

        var active = !getTheItem().isEmpty();
        if (getBlockState().getValue(DereferencerBlock.ACTIVE) != active)
            requireNonNull(level).setBlockAndUpdate(worldPosition, getBlockState().setValue(DereferencerBlock.ACTIVE, active));
    }

    @Override
    public void setTheItem(ItemStack itemStack) {
        super.setTheItem(itemStack);
        updateState();
    }

    @Override
    public ItemStack splitTheItem(int count) {
        var result = super.splitTheItem(count);
        updateState();
        return result;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        updateState();
    }
}
