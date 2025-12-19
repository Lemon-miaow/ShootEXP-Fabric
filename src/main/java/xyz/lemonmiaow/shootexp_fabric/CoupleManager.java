package xyz.lemonmiaow.shootexp_fabric;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoupleManager {
    private static final Map<UUID, Couple> activeCoupleMap = new HashMap<>();

    public static void addCouple(UUID uuid, Couple couple) {
        activeCoupleMap.put(uuid, couple);
    }

    public static boolean hasCouple(UUID uuid) {
        return activeCoupleMap.containsKey(uuid);
    }

    public static Couple getCouple(UUID uuid) {
        return activeCoupleMap.get(uuid);
    }

    public static void removeCouple(UUID uuid) {
        activeCoupleMap.remove(uuid);
    }

    public static void tickAll(net.minecraft.server.MinecraftServer server) {
        var toRemove = new java.util.ArrayList<UUID>();
        
        for (var entry : activeCoupleMap.entrySet()) {
            boolean shouldRemove = entry.getValue().tick(server);
            if (shouldRemove) {
                toRemove.add(entry.getKey());
            }
        }
        
        for (UUID uuid : toRemove) {
            activeCoupleMap.remove(uuid);
        }
    }
}
