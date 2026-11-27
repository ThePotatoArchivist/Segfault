package archives.tater.segfault.menu;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.block.StructFrameHelper;
import archives.tater.segfault.component.StructTemplate;
import archives.tater.segfault.registry.SegfaultModBlocks;
import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.registry.SegfaultModItems;
import archives.tater.segfault.registry.SegfaultModMenus;
import archives.tater.segfault.util.SegfaultUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static archives.tater.segfault.util.SegfaultUtil.betweenClosed;

public class StructDesignerMenu extends AbstractContainerMenu {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int BUTTON_CREATE = 0;
    private final ContainerLevelAccess access;
    private final Container container = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };
    public final @Nullable BoundingBox box;
    private String structName = "";

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public StructDesignerMenu(int containerId, Inventory inventory, Optional<BoundingBox> box) {
        this(containerId, inventory, box.orElse(null));
    }

    public StructDesignerMenu(int containerId, Inventory inventory, @Nullable BoundingBox box) {
        this(containerId, inventory, box, ContainerLevelAccess.NULL);
    }

    public StructDesignerMenu(int containerId, Inventory inventory, @Nullable BoundingBox box, ContainerLevelAccess access) {
        super(SegfaultModMenus.STRUCT_DESIGNER, containerId);
        this.access = access;
        this.box = box;

        addSlot(new Slot(container, SLOT_INPUT, 89, 39) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.is(SegfaultModItems.EMPTY_STRUCT_TEMPLATE);
            }
        });
        addSlot(new Slot(container, SLOT_OUTPUT, 143, 39) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }
        });
        addStandardInventorySlots(inventory, 8, 97);
    }

    public ItemStack getInput() {
        return container.getItem(SLOT_INPUT);
    }

    public ItemStack getOutput() {
        return container.getItem(SLOT_OUTPUT);
    }

    public void setOutput(ItemStack stack) {
        container.setItem(SLOT_OUTPUT, stack);
    }

    public void setStructName(String structName) {
        this.structName = structName;
    }

    public boolean canSaveStruct() {
        return !getInput().isEmpty() && getOutput().isEmpty() && box != null && !structName.isBlank() && Identifier.isValidPath(structName);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId != BUTTON_CREATE) return false;
        if (!canSaveStruct()) return false;
        access.execute((level, blockPos) -> {
            if (box == null || !StructFrameHelper.regionValid(level, box.inflatedBy(1))) {
                level.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS);
                return;
            }

            clear(level, box);
            var structTemplate = saveStruct((ServerLevel) level, player, box, structName);
            if (structTemplate == null) return;

            destroy(level, box);

            var input = getInput();
            var result = input.transmuteCopy(SegfaultModItems.STRUCT_TEMPLATE);
            result.set(SegfaultModComponents.STRUCT_TEMPLATE, structTemplate);
            setOutput(result);
            input.shrink(1);
        });
        setStructName("");
        return true;
    }

    private void clear(Level level, BoundingBox box) {
        for (var pos : betweenClosed(box)) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity == null) continue;
            blockEntity.preRemoveSideEffects(pos, blockEntity.getBlockState());
            if (blockEntity instanceof Clearable clearable)
                clearable.clearContent();
        }
    }

    private void destroy(Level level, BoundingBox box) {
        var state = Blocks.AIR.defaultBlockState();
        for (var pos : betweenClosed(box))
            level.setBlock(pos, state, Block.UPDATE_SKIP_ALL_SIDEEFFECTS | Block.UPDATE_CLIENTS);
    }

    public static @Nullable StructTemplate saveStruct(ServerLevel level, Player player, BoundingBox box, String name) {
        var id = Segfault.id(name);
        if (!StructureBlockEntity.saveStructure(
                level,
                id,
                new BlockPos(box.minX(), box.minY(), box.minZ()),
                new Vec3i(box.getXSpan(), box.getYSpan(), box.getZSpan()),
                true,
                player.getPlainTextName(),
                true,
                List.of()
        )) {
            Segfault.LOGGER.error("Failed to save structure!");
            return null;
        }
        return new StructTemplate(id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return SegfaultUtil.quickMoveStack(this, container, 1, player, slotIndex);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, SegfaultModBlocks.STRUCT_DESIGNER);
    }

    @Override
    public void removed(Player player) {
        access.execute((_, _) -> clearContainer(player, container));
    }
}
