package archives.tater.segfault.client.datagen;

import archives.tater.segfault.registry.SegfaultModBlocks;
import archives.tater.segfault.registry.SegfaultModItems;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createTrivialCube(SegfaultModBlocks.ALLOCATOR);
        blockModelGenerators.createTrivialCube(SegfaultModBlocks.DEREFERENCER);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(SegfaultModItems.POINTER, ModelTemplates.FLAT_ITEM);
    }
}
