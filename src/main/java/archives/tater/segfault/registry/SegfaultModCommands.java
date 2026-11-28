package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.data.AllocationManager;
import archives.tater.segfault.block.StructFrameHelper;
import archives.tater.segfault.data.PlayerPlacedBlocks;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.*;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos;
import static net.minecraft.commands.arguments.coordinates.ColumnPosArgument.columnPos;
import static net.minecraft.commands.arguments.coordinates.ColumnPosArgument.getColumnPos;

public class SegfaultModCommands {

    public static final Component RESET_SUCCESS = Component.translatable("commands.segfault.reset.success");

    public static final SimpleCommandExceptionType DETECT_FAIL = new SimpleCommandExceptionType(Component.translatable("commands.segfault.detect_frame.fail"));
    public static final SimpleCommandExceptionType PLAYER_PLACED_FAIL = new SimpleCommandExceptionType(Component.translatable("commands.segfault.show_player_placed.fail"));

    private SegfaultModCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(literal(Segfault.MOD_ID)
                .requires(hasPermission(LEVEL_GAMEMASTERS))
                .then(literal("reset_allocations").executes(context -> {
                    AllocationManager.get(context.getSource().getServer()).reset();
                    context.getSource().sendSuccess(() -> RESET_SUCCESS, true);
                    return Command.SINGLE_SUCCESS;
                }))
                .then(literal("detect_frame")
                        .then(argument("start", blockPos()).executes(context -> {
                            var box = StructFrameHelper.findRegion(context.getSource().getLevel(), getLoadedBlockPos(context, context.getSource().getLevel(), "start"));
                            if (box == null) throw DETECT_FAIL.create();
                            context.getSource().sendSuccess(() -> Component.translatable("commands.segfault.detect_frame.success", box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()), false);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("show_player_placed")
                        .then(argument("pos", columnPos()).executes(context -> {
                            var pos = getColumnPos(context, "pos").toChunkPos();
                            var placedBlocks = context.getSource().getLevel().getChunk(pos.x(), pos.z()).getAttached(SegfaultModAttachments.PLAYER_PLACED_BLOCKS);
                            if (placedBlocks == null) throw PLAYER_PLACED_FAIL.create();

                            var nbt = PlayerPlacedBlocks.ATTACHMENT_CODEC.encodeStart(NbtOps.INSTANCE, placedBlocks).getOrThrow();
                            context.getSource().sendSuccess(() -> Component.translatable("commands.segfault.show_player_placed.success", pos.x(), pos.z(), NbtUtils.toPrettyComponent(nbt)), false);

                            return Command.SINGLE_SUCCESS;
                        }))
                )
        );
    }
}
