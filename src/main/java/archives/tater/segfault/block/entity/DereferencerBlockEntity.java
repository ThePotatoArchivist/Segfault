package archives.tater.segfault.block.entity;

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

import org.jspecify.annotations.Nullable;

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
}
