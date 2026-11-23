package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.allocation.AllocationManager;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.*;

public class SegfaultModCommands {
    private SegfaultModCommands() {}

    public static final Component RESET_SUCCESS = Component.translatable("commands.segfault.reset.success");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(literal(Segfault.MOD_ID)
                .requires(hasPermission(LEVEL_GAMEMASTERS))
                .then(literal("reset").executes(context -> {
                    AllocationManager.get(context.getSource().getServer()).reset();
                    context.getSource().sendSuccess(() -> RESET_SUCCESS, true);
                    return Command.SINGLE_SUCCESS;
                }))
        );
    }
}
