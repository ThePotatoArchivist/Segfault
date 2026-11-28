package archives.tater.segfault.data;

import archives.tater.segfault.registry.SegfaultModAttachments;
import archives.tater.segfault.registry.SegfaultModDimensions;
import archives.tater.segfault.util.PatchedMap;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static archives.tater.segfault.util.SegfaultUtil.entryListMapCodec;

public class PlayerPlacedBlocks {
    public static final Codec<PatchedMap<BlockPos, Block>> ATTACHMENT_CODEC = PatchedMap.codec(entryListMapCodec(BlockPos.CODEC, BuiltInRegistries.BLOCK.byNameCodec()));

    public static void onPlace(Level level, BlockPos pos, BlockState state) {
        if (level.dimension() != SegfaultModDimensions.HEAP) return;
        var chunk = level.getChunk(pos);
        if (!chunk.hasAttached(SegfaultModAttachments.PLAYER_PLACED_BLOCKS)) return;
        PatchedMap.put(chunk, SegfaultModAttachments.PLAYER_PLACED_BLOCKS, pos, state.getBlock());
    }

    public static boolean shouldDrop(Level level, BlockPos pos, BlockState state) {
        if (level.dimension() != SegfaultModDimensions.HEAP) return true;
        var chunk = level.getChunk(pos);
        var block = PatchedMap.remove(chunk, SegfaultModAttachments.PLAYER_PLACED_BLOCKS, pos);
        return block != null && state.is(block);
    }
}
