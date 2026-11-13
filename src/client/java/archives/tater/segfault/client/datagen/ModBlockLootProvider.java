package archives.tater.segfault.client.datagen;

import archives.tater.segfault.registry.SegfaultModBlocks;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;

import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootProvider extends FabricBlockLootSubProvider {
    public ModBlockLootProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    public void generate() {
        add(SegfaultModBlocks.ALLOCATOR, this::createNameableBlockEntityTable);
        add(SegfaultModBlocks.DEREFERENCER, this::createNameableBlockEntityTable);
    }
}
