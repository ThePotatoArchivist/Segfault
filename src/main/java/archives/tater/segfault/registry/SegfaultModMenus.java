package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.menu.*;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public interface SegfaultModMenus {
    MenuType<SingleSlotMenu> SINGLE_SLOT = register("single_slot", SingleSlotMenu::new);
    MenuType<ConstructorMenu> CONSTRUCTOR = register("constructor", ConstructorMenu::new);
    MenuType<DereferencerMenu> DEREFERENCER = register("dereferencer", DereferencerMenu::new);
    MenuType<DestructorMenu> DESTRUCTOR = register("destructor", DestructorMenu::new);
    MenuType<StructDesignerMenu> STRUCT_DESIGNER = register("struct_designer", StructDesignerMenu::new, ByteBufCodecs.optional(BoundingBox.STREAM_CODEC));

    private static <T extends AbstractContainerMenu> MenuType<T> register(String path, MenuType.MenuSupplier<T> supplier) {
        return Registry.register(BuiltInRegistries.MENU, Segfault.id(path), new MenuType<>(supplier, FeatureFlagSet.of()));
    }

    private static <T extends AbstractContainerMenu, D> ExtendedMenuType<T, D> register(String path, ExtendedMenuType.ExtendedFactory<T, D> supplier, StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        return Registry.register(BuiltInRegistries.MENU, Segfault.id(path), new ExtendedMenuType<>(supplier, codec));
    }

    static void init() {

    }
}
