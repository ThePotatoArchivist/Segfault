package archives.tater.segfault.client.datagen;

import archives.tater.segfault.block.entity.AllocatorBlockEntity;
import archives.tater.segfault.block.entity.DereferencerBlockEntity;
import archives.tater.segfault.block.entity.DestructorBlockEntity;
import archives.tater.segfault.registry.SegfaultModBlocks;
import archives.tater.segfault.registry.SegfaultModCreativeTabs;
import archives.tater.segfault.registry.SegfaultModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(SegfaultModBlocks.ALLOCATOR, "Allocator");
        translationBuilder.add(SegfaultModBlocks.DEREFERENCER, "Dereferencer");
        translationBuilder.add(SegfaultModBlocks.DESTRUCTOR, "Destructor");

        translationBuilder.add(SegfaultModItems.POINTER, "Pointer");
        translationBuilder.add(SegfaultModItems.STRUCT_TEMPLATE, "Struct Template");
        translationBuilder.add(SegfaultModItems.PORTABLE_DEREFERENCER, "Portable Dereferencer");

        translationBuilder.add(getKey(SegfaultModCreativeTabs.SEGFAULT.getDisplayName()), "Segfault");

        translationBuilder.add("item.segfault.pointer.value", "(%s, %s, %s)");
        translationBuilder.add("item.segfault.pointer.null", "NULL");
        translationBuilder.add("item.segfault.pointer.target", "→ %s");
        translationBuilder.add("item.segfault.struct_template.dimensions", "%sx%sx%s");
        translationBuilder.add(getKey(AllocatorBlockEntity.DEFAULT_NAME), "Allocator");
        translationBuilder.add(getKey(DereferencerBlockEntity.DEFAULT_NAME), "Dereferencer");
        translationBuilder.add(getKey(DestructorBlockEntity.DEFAULT_NAME), "Destructor");
    }

    private static String getKey(Component translatable) {
        return ((TranslatableContents) translatable.getContents()).getKey();
    }
}
