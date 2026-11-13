package archives.tater.segfault.client.datagen;

import archives.tater.segfault.block.entity.AllocatorBlockEntity;
import archives.tater.segfault.registry.SegfaultModBlocks;
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

        translationBuilder.add(SegfaultModItems.POINTER, "Pointer");

        translationBuilder.add("item.segfault.pointer.value", "→ (%s, %s)");
        translationBuilder.add("item.segfault.pointer.null", "→ NULL");
        translationBuilder.add(getKey(AllocatorBlockEntity.DEFAULT_NAME), "Allocate");
    }

    private static String getKey(Component translatable) {
        return ((TranslatableContents) translatable.getContents()).getKey();
    }
}
