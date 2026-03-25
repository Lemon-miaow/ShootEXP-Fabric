package xyz.lemonmiaow.shootexp_fabric;

import xyz.lemonmiaow.shootexp_fabric.config.ModConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SneakDetector {
    private static final Map<UUID, Boolean> sneakStates = new HashMap<>();

    public static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean currentSneaking = player.isShiftKeyDown();
            Boolean lastSneaking = sneakStates.get(player.getUUID());

            if (lastSneaking != null && !lastSneaking && currentSneaking) {
                onPlayerStartSneak(player);
            }

            sneakStates.put(player.getUUID(), currentSneaking);
        }

        CoupleManager.tickAll(server);

        PlayerStatusManager.tickAll(server);
    }

    private static void onPlayerStartSneak(ServerPlayer attacker) {
        Entity partner = Util.getNearestEntity(
                attacker,
                ModConfig.getAttackDistance(),
                ModConfig.getEntityTypes()
        );

        if (partner == null) {
            return;
        }

        UUID attackerUuid = attacker.getUUID();

        if (!CoupleManager.hasCouple(attackerUuid)) {
            Couple couple = new Couple(attacker, partner);
            CoupleManager.addCouple(attackerUuid, couple);
        }

        Couple couple = CoupleManager.getCouple(attackerUuid);
        couple.setDefender(partner);
        couple.attack();

        SneakDetector.playSound(attacker, ModConfig.getSoundAttack());
    }

    public static void playSound(ServerPlayer player, String soundId) {
        try {
            Identifier id = Identifier.parse(soundId);
            SoundEvent sound = SoundEvent.createVariableRangeEvent(id);
            player.level().playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    net.minecraft.core.Holder.direct(sound),
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
            );
        } catch (Exception e) {
            ShootexpFabric.LOGGER.warn("Failed to play sound: " + soundId, e);
        }
    }
}
