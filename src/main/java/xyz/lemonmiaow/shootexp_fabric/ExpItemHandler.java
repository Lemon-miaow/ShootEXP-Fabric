package xyz.lemonmiaow.shootexp_fabric;

import xyz.lemonmiaow.shootexp_fabric.config.Language;
import xyz.lemonmiaow.shootexp_fabric.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class ExpItemHandler {

    public static InteractionResult onUseBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);

        if (!ExpItem.isExpItem(stack)) {
            return InteractionResult.PASS;
        }

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        consumeExpItem(player, world, stack);

        return InteractionResult.SUCCESS_SERVER;
    }

    public static InteractionResult onUseItem(Player player, Level world, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!ExpItem.isExpItem(stack)) {
            return InteractionResult.PASS;
        }

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        consumeExpItem(player, world, stack);

        return InteractionResult.SUCCESS_SERVER;
    }

    private static void consumeExpItem(Player player, Level world, ItemStack stack) {
        String owner = ExpItem.getOwner(stack);
        String recipient = ExpItem.getRecipient(stack);
        int amount = ExpItem.getAmount(stack);

        player.giveExperiencePoints(amount);

        if (player instanceof ServerPlayer serverPlayer) {
            SneakDetector.playSound(serverPlayer, ModConfig.getSoundEat());
        }

        String msg = Language.getString("shootexp.message.eat")
                .replace("%PLAYER%", player.getName().getString())
                .replace("%OWNER%", owner)
                .replace("%RECIPIENT%", recipient)
                .replace("%AMOUNT%", String.valueOf(amount));
        Component component = Component.literal(msg);

        if (ModConfig.isPrivateMessage()) {
            player.sendSystemMessage(component);
            MinecraftServer server = world.getServer();
            if (server != null) {
                notifyPlayer(server, owner, component);
                notifyPlayer(server, recipient, component);
            }
        } else {
            MinecraftServer server = world.getServer();
            if (server != null) {
                for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                    serverPlayer.sendSystemMessage(component);
                }
            }
        }

        stack.shrink(1);
    }

    private static void notifyPlayer(MinecraftServer server, String playerName, Component message) {
        ServerPlayer target = server.getPlayerList().getPlayerByName(playerName);
        if (target != null) {
            target.sendSystemMessage(message);
        }
    }
}
