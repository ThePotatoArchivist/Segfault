package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.menu.AllocatorMenu;
import archives.tater.segfault.menu.DereferencerMenu;
import archives.tater.segfault.menu.SingleSlotMenu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface SegfaultModMenus {
    MenuType<SingleSlotMenu> SINGLE_SLOT = register("single_slot", SingleSlotMenu::new);
    MenuType<AllocatorMenu> ALLOCATOR = register("allocator", AllocatorMenu::new);
    MenuType<DereferencerMenu> DEREFERENCER = register("dereferencer", DereferencerMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(String path, MenuType.MenuSupplier<T> supplier) {
        return Registry.register(BuiltInRegistries.MENU, Segfault.id(path), new MenuType<>(supplier, FeatureFlagSet.of()));
    }

    static void init() {

    }
}
