package xyz.lemonmiaow.shootexp_fabric;

import xyz.lemonmiaow.shootexp_fabric.config.Language;
import xyz.lemonmiaow.shootexp_fabric.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class Couple {
    private final ServerPlayer attacker;
    private Entity defender;
    private int numOfAttack;
    private long lastAttackTick;

    public Couple(ServerPlayer attacker, Entity defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.numOfAttack = 0;
        this.lastAttackTick = attacker.level().getServer().getTickCount();
    }

    public void setDefender(Entity defender) {
        this.defender = defender;
    }

    public void attack() {
        numOfAttack++;
        lastAttackTick = attacker.level().getServer().getTickCount();
    }

    public boolean tick(MinecraftServer server) {
        long currentTick = server.getTickCount();

        if (currentTick - lastAttackTick > ModConfig.getAttackTimeout()) {
            return true;
        }

        if (attacker.isRemoved() || !attacker.isAlive()) {
            return true;
        }

        UUID attackerUuid = attacker.getUUID();
        if (!PlayerStatusManager.hasStatus(attackerUuid)) {
            PlayerStatusManager.addStatus(attackerUuid, new PlayerStatus());
        }

        PlayerStatus status = PlayerStatusManager.getStatus(attackerUuid);
        int requiredTimes = status.getRequiredAttackTimes();

        if (numOfAttack >= requiredTimes) {
            int expAmount = status.ejaculation();
            sendShootMessage(expAmount);

            String sound = expAmount > 0 ? ModConfig.getSoundShoot() : ModConfig.getSoundShootNoExp();
            SneakDetector.playSound(attacker, sound);

            if (expAmount > 0) {
                ItemStack expItem = ExpItem.create(attacker.getName().getString(), 
                        defender.getName().getString(), expAmount);
                attacker.level().addFreshEntity(
                        new net.minecraft.world.entity.item.ItemEntity(
                                attacker.level(),
                                attacker.getX(),
                                attacker.getY(),
                                attacker.getZ(),
                                expItem
                        )
                );
            }

            return true;
        }

        return false;
    }

    private void sendShootMessage(int expAmount) {
        String msgKey = expAmount > 0 ? "shootexp.message.shoot" : "shootexp.message.shoot_no_exp";
        String msg = Language.getString(msgKey)
                .replace("%ATTACKER%", attacker.getName().getString())
                .replace("%DEFENDER%", defender.getName().getString())
                .replace("%TIMES%", String.valueOf(numOfAttack))
                .replace("%AMOUNT%", String.valueOf(expAmount));

        Component component = Component.literal(msg);

        if (ModConfig.isPrivateMessage()) {
            attacker.sendSystemMessage(component);
            if (defender instanceof ServerPlayer defenderPlayer) {
                defenderPlayer.sendSystemMessage(component);
            }
        } else {
            MinecraftServer server = attacker.level().getServer();
            if (server != null) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    player.sendSystemMessage(component);
                }
            }
        }
    }
}
