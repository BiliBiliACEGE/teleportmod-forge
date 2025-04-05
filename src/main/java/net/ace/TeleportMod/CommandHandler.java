package net.ace.TeleportMod;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "teleportmod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommandHandler {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        // 注册 /tpz <x> <y> <z> 指令
        dispatcher.register(
                Commands.literal("tpz")
                        .requires(source -> source.hasPermission(0)) // 允许非OP玩家使用
                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(context -> executeTeleportToCoordinates(context))
                                        )
                                ))
        );

        // 注册 /tph <玩家名> 指令
        dispatcher.register(
                Commands.literal("tph")
                        .requires(source -> source.hasPermission(0))
                        .then(Commands.argument("target", StringArgumentType.string())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(ctx.getSource().getOnlinePlayerNames(), builder))
                                .executes(context -> executeTeleportToPlayer(context))
                        )
        );
    }

    // 实现 /tp 指令逻辑
    private static int executeTeleportToCoordinates(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            double x = DoubleArgumentType.getDouble(context, "x");
            double y = DoubleArgumentType.getDouble(context, "y");
            double z = DoubleArgumentType.getDouble(context, "z");

            // 校验坐标合法性
            if (!isWithinWorldBorder(player.serverLevel(), x, y, z)) {
                context.getSource().sendFailure(Component.translatable("Command.pot.error.info"));
                return 0;
            }

            // 执行传送
            player.teleportTo(
                    player.serverLevel(),
                    x, y, z,
                    player.getYRot(), player.getXRot()
            );
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.translatable("command.error.info" + e.getMessage()));
            return 0;
        }
    }

    // 实现 /tphere 指令逻辑
    private static int executeTeleportToPlayer(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer sourcePlayer = context.getSource().getPlayer();
            String targetName = StringArgumentType.getString(context, "target");
            ServerPlayer targetPlayer = context.getSource().getServer().getPlayerList().getPlayerByName(targetName);

            if (targetPlayer == null) {
                context.getSource().sendFailure(Component.translatable("command.player.error.info"));
                return 0;
            }

            // 执行传送
            sourcePlayer.teleportTo(
                    targetPlayer.serverLevel(),
                    targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(),
                    targetPlayer.getYRot(), targetPlayer.getXRot()
            );
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.translatable("command.error.info" + e.getMessage()));
            return 0;
        }
    }

    // 校验坐标是否在世界边界内
    private static boolean isWithinWorldBorder(ServerLevel level, double x, double y, double z) {
        return level.getWorldBorder().isWithinBounds(new BlockPos((int) x, (int) y, (int) z));
    }
}