package archives.tater.segfault.client.datagen;

import archives.tater.segfault.block.StructDesignerBlock;
import archives.tater.segfault.block.entity.ConstructorBlockEntity;
import archives.tater.segfault.block.entity.DereferencerBlockEntity;
import archives.tater.segfault.block.entity.DestructorBlockEntity;
import archives.tater.segfault.client.SegfaultClient;
import archives.tater.segfault.client.screen.StructDesignerScreen;
import archives.tater.segfault.registry.SegfaultModBlocks;
import archives.tater.segfault.registry.SegfaultModCommands;
import archives.tater.segfault.registry.SegfaultModCreativeTabs;
import archives.tater.segfault.registry.SegfaultModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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
        translationBuilder.add(SegfaultModBlocks.CONSTRUCTOR, "Constructor");
        translationBuilder.add(SegfaultModBlocks.DEREFERENCER, "Dereferencer");
        translationBuilder.add(SegfaultModBlocks.DESTRUCTOR, "Destructor");
        translationBuilder.add(SegfaultModBlocks.STRUCT_DESIGNER, "Struct Designer");
        translationBuilder.add(SegfaultModBlocks.STRUCT_FRAME, "Struct Frame");

        translationBuilder.add(SegfaultModItems.POINTER, "Pointer");
        translationBuilder.add(SegfaultModItems.EMPTY_STRUCT_TEMPLATE, "Empty Struct Template");
        translationBuilder.add(SegfaultModItems.STRUCT_TEMPLATE, "Struct Template");
        translationBuilder.add(SegfaultModItems.PORTABLE_DEREFERENCER, "Portable Dereferencer");

        translationBuilder.add(getKey(SegfaultModCreativeTabs.SEGFAULT.getDisplayName()), "Segfault");

        translationBuilder.add("item.segfault.pointer.value", "(%s, %s, %s)");
        translationBuilder.add("item.segfault.pointer.null", "NULL");
        translationBuilder.add("item.segfault.pointer.target", "→ %s");
        translationBuilder.add("item.segfault.struct_template.dimensions", "%sx%sx%s");
        translationBuilder.add(getKey(ConstructorBlockEntity.DEFAULT_NAME), "Constructor");
        translationBuilder.add(getKey(DereferencerBlockEntity.DEFAULT_NAME), "Dereferencer");
        translationBuilder.add(getKey(DestructorBlockEntity.DEFAULT_NAME), "Destructor");
        translationBuilder.add(getKey(StructDesignerBlock.CONTAINER_TITLE), "Struct Designer");
        translationBuilder.add(getKey(StructDesignerScreen.PLACEHOLDER_TEXT), "Struct Name");
        translationBuilder.add("screen.segfault.struct_designer.dimensions", "%sx%sx%s");
        translationBuilder.add(getKey(StructDesignerScreen.NOT_FOUND_TEXT), "Not Found");

        translationBuilder.add(getKey(SegfaultModCommands.RESET_SUCCESS), "Reset all allocations");
        translationBuilder.add(getKey(getMessage(SegfaultModCommands.DETECT_FAIL)), "Could not detect a frame");
        translationBuilder.add("commands.segfault.detect_frame.success", "Detected frame from (%s, %s %s) to (%s, %s, %s)");
        translationBuilder.add("commands.segfault.show_player_placed.success", "Player placed blocks in chunk (%s, %s): %s");
        translationBuilder.add(getKey(getMessage(SegfaultModCommands.PLAYER_PLACED_FAIL)), "Player placed blocks not tracked for this chunk");

        translationBuilder.add(SegfaultClient.keyDebugChunkGrid.getName(), "Show Chunk Grid");
        translationBuilder.add(getKey(SegfaultClient.DEBUG_CHUNK_GRID_ON), "Chunk grid: shown");
        translationBuilder.add(getKey(SegfaultClient.DEBUG_CHUNK_GRID_OFF), "Chunk grid: hidden");
    }

    private static Component getMessage(SimpleCommandExceptionType exception) {
        return (Component) exception.create().getRawMessage();
    }

    private static String getKey(Component translatable) {
        return ((TranslatableContents) translatable.getContents()).getKey();
    }
}
