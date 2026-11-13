package archives.tater.segfault.block.entity;

import archives.tater.segfault.menu.SingleSlotMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.ticks.ContainerSingleItem;

import org.jspecify.annotations.Nullable;

import java.util.List;

import static java.util.Objects.requireNonNullElseGet;

public abstract class BaseSingleItemContainerBlockEntity extends BlockEntity implements ContainerSingleItem.BlockContainerSingleItem, MenuProvider, Nameable {
    private @Nullable Component name;
    private ItemStack item = ItemStack.EMPTY;

    public BaseSingleItemContainerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        name = parseCustomNameSafe(input, "CustomName");
        item = input.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("CustomName", ComponentSerialization.CODEC, name);
        if (!item.isEmpty())
            output.store("item", ItemStack.CODEC, item);
    }

    @Override
    public Component getName() {
        return requireNonNullElseGet(name, this::getDefaultName);
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public @Nullable Component getCustomName() {
        return name;
    }

    protected abstract Component getDefaultName();

    @Override
    public ItemStack getTheItem() {
        return item;
    }

    @Override
    public void setTheItem(ItemStack itemStack) {
        item = itemStack;
    }

    @Override
    public ItemStack splitTheItem(int count) {
        var result = BlockContainerSingleItem.super.splitTheItem(count);
        setChanged();
        return result;
    }

    @Override
    public BlockEntity getContainerBlockEntity() {
        return this;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SingleSlotMenu(containerId, inventory, this);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        name = components.get(DataComponents.CUSTOM_NAME);
        item = components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, name);
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(item)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard("CustomName");
        output.discard("item");
    }
}
