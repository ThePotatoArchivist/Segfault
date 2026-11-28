package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.data.PlayerPlacedBlocks;
import archives.tater.segfault.util.PatchedMap;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.block.Block;

public interface SegfaultModAttachments {
    AttachmentType<GlobalPos> ENTRY_POS = AttachmentRegistry.createPersistent(Segfault.id("entry_pos"), GlobalPos.CODEC);
    AttachmentType<PatchedMap<BlockPos, Block>> PLAYER_PLACED_BLOCKS = AttachmentRegistry.createPersistent(Segfault.id("player_placed_blocks"), PlayerPlacedBlocks.ATTACHMENT_CODEC);

    static void init() {

    }
}
