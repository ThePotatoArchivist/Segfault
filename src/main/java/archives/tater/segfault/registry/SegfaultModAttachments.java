package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.core.GlobalPos;

public interface SegfaultModAttachments {
    AttachmentType<GlobalPos> ENTRY_POS = AttachmentRegistry.createPersistent(Segfault.id("entry_pos"), GlobalPos.CODEC);

    static void init() {

    }
}
