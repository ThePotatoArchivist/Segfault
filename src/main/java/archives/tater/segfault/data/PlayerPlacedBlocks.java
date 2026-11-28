package archives.tater.segfault.data;

import archives.tater.segfault.registry.SegfaultModAttachments;
import archives.tater.segfault.registry.SegfaultModDimensions;
import archives.tater.segfault.util.PatchedMap;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

import static archives.tater.segfault.util.SegfaultUtil.entryListMapCodec;

public class PlayerPlacedBlocks {
    public static final Codec<PatchedMap<BlockPos, Block>> ATTACHMENT_CODEC = PatchedMap.codec(entryListMapCodec(BlockPos.CODEC, BuiltInRegistries.BLOCK.byNameCodec()));

    public static void onPlace(Level level, BlockPos pos, BlockState state) {
        if (level.dimension() != SegfaultModDimensions.HEAP) return;
        var adjustedPos = fixPos(pos, state);
        var chunk = level.getChunk(adjustedPos);
        if (!chunk.hasAttached(SegfaultModAttachments.PLAYER_PLACED_BLOCKS)) return;

        PatchedMap.put(chunk, SegfaultModAttachments.PLAYER_PLACED_BLOCKS, adjustedPos, state.getBlock());
    }

    private static BlockPos fixPos(BlockPos pos, BlockState state) {
        // Beds drop at the head but place at the foot
        if (state.hasProperty(BedBlock.PART) && state.hasProperty(BedBlock.FACING) && state.getValue(BedBlock.PART) == BedPart.FOOT)
            return pos.relative(state.getValue(BedBlock.FACING));

        return pos;
    }

    public static boolean shouldDrop(Level level, BlockPos pos, BlockState state) {
        if (level.dimension() != SegfaultModDimensions.HEAP || !state.getBlock().asItem().canFitInsideContainerItems()) return true;
        var chunk = level.getChunk(pos);
        var block = PatchedMap.remove(chunk, SegfaultModAttachments.PLAYER_PLACED_BLOCKS, pos);
        return block != null && state.is(block);
    }
}
