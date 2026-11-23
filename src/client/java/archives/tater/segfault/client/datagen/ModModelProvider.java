package archives.tater.segfault.client.datagen;

import archives.tater.segfault.block.StructFrameBlock;
import archives.tater.segfault.registry.SegfaultModBlocks;
import archives.tater.segfault.registry.SegfaultModItems;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.world.level.block.Block;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;
import static net.minecraft.client.data.models.BlockModelGenerators.variant;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createTrivialCube(SegfaultModBlocks.CONSTRUCTOR);
        blockModelGenerators.createTrivialCube(SegfaultModBlocks.DEREFERENCER);
        blockModelGenerators.createTrivialCube(SegfaultModBlocks.DESTRUCTOR);

        createFrame(blockModelGenerators, SegfaultModBlocks.STRUCT_FRAME);
    }

    private static void createFrame(BlockModelGenerators blockModelGenerators, Block block) {
        var columnModel = new Variant(TexturedModel.COLUMN.createWithSuffix(block, "_column", blockModelGenerators.modelOutput));
        blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(StructFrameBlock.SHAPE)
                        .select(StructFrameBlock.Shape.Y, variant(columnModel))
                        .select(StructFrameBlock.Shape.Z, variant(columnModel.with(BlockModelGenerators.X_ROT_90)))
                        .select(StructFrameBlock.Shape.X, variant(columnModel.with(BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90))))
                        .select(StructFrameBlock.Shape.NONE, plainVariant(TexturedModel.CUBE.create(block, blockModelGenerators.modelOutput)))
                )
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(SegfaultModItems.POINTER, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(SegfaultModItems.STRUCT_TEMPLATE, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(SegfaultModItems.PORTABLE_DEREFERENCER, ModelTemplates.FLAT_ITEM);
    }
}
