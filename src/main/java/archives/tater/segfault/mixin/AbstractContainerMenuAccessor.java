package archives.tater.segfault.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

@Mixin(AbstractContainerMenu.class)
public interface AbstractContainerMenuAccessor {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Invoker
    boolean callMoveItemStackTo(final ItemStack itemStack, final int startSlot, final int endSlot, final boolean backwards);
}
