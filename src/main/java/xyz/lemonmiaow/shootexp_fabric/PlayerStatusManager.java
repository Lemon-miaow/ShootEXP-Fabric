package xyz.lemonmiaow.shootexp_fabric;

import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatusManager {
    private static final Map<UUID, PlayerStatus> statusMap = new HashMap<>();

    public static void addStatus(UUID uuid, PlayerStatus status) {
        statusMap.put(uuid, status);
    }

    public static boolean hasStatus(UUID uuid) {
        return statusMap.containsKey(uuid);
    }

    public static PlayerStatus getStatus(UUID uuid) {
        return statusMap.get(uuid);
    }

    public static PlayerStatus getOrCreate(UUID uuid) {
        if (!statusMap.containsKey(uuid)) {
            statusMap.put(uuid, new PlayerStatus());
        }
        return statusMap.get(uuid);
    }

    public static void tickAll(MinecraftServer server) {
        long currentTick = server.getTickCount();
        for (PlayerStatus status : statusMap.values()) {
            status.tick(currentTick);
        }
    }
}
