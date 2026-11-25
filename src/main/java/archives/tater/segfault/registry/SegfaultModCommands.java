package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.allocation.AllocationManager;
import archives.tater.segfault.block.StructFrameBlock;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.*;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos;

public class SegfaultModCommands {

    public static final Component RESET_SUCCESS = Component.translatable("commands.segfault.reset.success");

    public static final SimpleCommandExceptionType DETECT_FAIL = new SimpleCommandExceptionType(Component.translatable("commands.segfault.detect_frame.fail"));

    private SegfaultModCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(literal(Segfault.MOD_ID)
                .requires(hasPermission(LEVEL_GAMEMASTERS))
                .then(literal("reset").executes(context -> {
                    AllocationManager.get(context.getSource().getServer()).reset();
                    context.getSource().sendSuccess(() -> RESET_SUCCESS, true);
                    return Command.SINGLE_SUCCESS;
                }))
                .then(literal("detect_frame")
                        .then(argument("start", blockPos()).executes(context -> {
                            var box = StructFrameBlock.findRegion(context.getSource().getLevel(), getLoadedBlockPos(context, context.getSource().getLevel(), "start"));
                            if (box == null) throw DETECT_FAIL.create();
                            context.getSource().sendSuccess(() -> Component.translatable("commands.segfault.detect_frame.success", box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()), false);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
        );
    }
}
