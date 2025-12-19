package xyz.lemonmiaow.shootexp_fabric;

import xyz.lemonmiaow.shootexp_fabric.config.Language;
import xyz.lemonmiaow.shootexp_fabric.config.ModConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                 CommandBuildContext registryAccess,
                                 net.minecraft.commands.Commands.CommandSelection environment) {

        dispatcher.register(
                net.minecraft.commands.Commands.literal("shootexp")
                        .executes(ModCommands::help)
                        .then(net.minecraft.commands.Commands.literal("help")
                                .executes(ModCommands::help))
                        .then(net.minecraft.commands.Commands.literal("status")
                                .executes(ModCommands::statusSelf)
                                .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                                        .requires(source -> source.hasPermission(2))
                                        .executes(ModCommands::statusOther)))
                        .then(net.minecraft.commands.Commands.literal("item")
                                .requires(source -> source.hasPermission(2))
                                .then(net.minecraft.commands.Commands.argument("owner", StringArgumentType.word())
                                        .then(net.minecraft.commands.Commands.argument("recipient", StringArgumentType.word())
                                                .then(net.minecraft.commands.Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(ModCommands::item)))))
                        .then(net.minecraft.commands.Commands.literal("restore")
                                .requires(source -> source.hasPermission(2))
                                .then(net.minecraft.commands.Commands.literal("all")
                                        .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                                                .executes(ModCommands::restoreAll)))
                                .then(net.minecraft.commands.Commands.literal("times")
                                        .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                                                .then(net.minecraft.commands.Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(ModCommands::restoreTimes))))
                                .then(net.minecraft.commands.Commands.literal("stock")
                                        .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                                                .then(net.minecraft.commands.Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(ModCommands::restoreStock)))))
                        .then(net.minecraft.commands.Commands.literal("set")
                                .requires(source -> source.hasPermission(2))
                                .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                                        .then(net.minecraft.commands.Commands.argument("times", IntegerArgumentType.integer(0))
                                                .then(net.minecraft.commands.Commands.argument("stock", IntegerArgumentType.integer(0))
                                                        .executes(ModCommands::set)))))
                        .then(net.minecraft.commands.Commands.literal("reload")
                                .requires(source -> source.hasPermission(2))
                                .executes(ModCommands::reload))
        );
    }

    private static int help(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.help.title")), false);
        source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.help.help")), false);
        source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.help.status")), false);

        if (source.hasPermission(2)) {
            source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.help.item")), false);
            source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.help.restore")), false);
            source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.help.set")), false);
            source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.help.reload")), false);
        }
        return 1;
    }

    private static int statusSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal(Language.getString("shootexp.command.player_only")));
            return 0;
        }
        return showStatus(source, player);
    }

    private static int statusOther(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        return showStatus(source, player);
    }

    private static int showStatus(CommandSourceStack source, ServerPlayer player) {
        UUID uuid = player.getUUID();
        int times = 0;
        int stock = ModConfig.getMaxStock();

        if (PlayerStatusManager.hasStatus(uuid)) {
            PlayerStatus status = PlayerStatusManager.getStatus(uuid);
            times = status.getTimesOfShoot();
            stock = status.getStock();
        }

        String msg = Language.getString("shootexp.command.status")
                .replace("%PLAYER%", player.getName().getString())
                .replace("%TIMES%", String.valueOf(times))
                .replace("%STOCK%", String.valueOf(stock));
        source.sendSuccess(() -> Component.literal(msg), false);
        return 1;
    }

    private static int item(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal(Language.getString("shootexp.command.player_only")));
            return 0;
        }

        String owner = StringArgumentType.getString(context, "owner");
        String recipient = StringArgumentType.getString(context, "recipient");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        ItemStack expItem = ExpItem.create(owner, recipient, amount);
        player.getInventory().add(expItem);

        return 1;
    }

    private static int restoreAll(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        UUID uuid = player.getUUID();

        PlayerStatus status = PlayerStatusManager.getOrCreate(uuid);
        status.restoreShootFull();
        status.restoreStockFull();

        source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.restore")), false);
        return 1;
    }

    private static int restoreTimes(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UUID uuid = player.getUUID();

        PlayerStatus status = PlayerStatusManager.getOrCreate(uuid);
        status.restoreShoot(amount);

        source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.restore")), false);
        return 1;
    }

    private static int restoreStock(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UUID uuid = player.getUUID();

        PlayerStatus status = PlayerStatusManager.getOrCreate(uuid);
        status.restoreStock(amount);

        source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.restore")), false);
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int times = IntegerArgumentType.getInteger(context, "times");
        int stock = IntegerArgumentType.getInteger(context, "stock");
        UUID uuid = player.getUUID();

        PlayerStatus status = PlayerStatusManager.getOrCreate(uuid);
        status.setTimesOfShoot(times);
        status.setStock(stock);

        source.sendSuccess(() -> Component.literal(Language.getString("shootexp.command.set")), false);
        return 1;
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        ModConfig.reload();
        context.getSource().sendSuccess(
                () -> Component.literal(Language.getString("shootexp.command.reloaded")), false);
        return 1;
    }
}
