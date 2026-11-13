package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;

import static net.minecraft.util.Util.makeDescriptionId;

public interface SegfaultModCreativeTabs {
    CreativeModeTab SEGFAULT = register("segfault", FabricCreativeModeTab.builder()
            .icon(SegfaultModItems.POINTER::getDefaultInstance)
            .displayItems((parameters, output) -> {
                output.accept(SegfaultModItems.ALLOCATOR);
                output.accept(SegfaultModItems.DEREFERENCER);
                output.accept(SegfaultModItems.DESTRUCTOR);
                output.accept(SegfaultModItems.POINTER);
                output.accept(SegfaultModItems.PORTABLE_DEREFERENCER);
            })
    );

    private static CreativeModeTab register(String path, CreativeModeTab.Builder builder) {
        return register(Segfault.id(path), builder);
    }

    private static CreativeModeTab register(Identifier id, CreativeModeTab.Builder builder) {
        return Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB, id, builder
                        .title(Component.translatable(makeDescriptionId("itemGroup", id)))
                        .build()
        );
    }

    static void init() {

    }
}
