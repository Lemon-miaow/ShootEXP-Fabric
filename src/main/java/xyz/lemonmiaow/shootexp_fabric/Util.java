package xyz.lemonmiaow.shootexp_fabric;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class Util {

    public static Entity getNearestEntity(Entity self, double range, List<String> includes) {
        Level world = self.level();
        
        AABB box = new AABB(
                self.getX() - range,
                self.getY() - range,
                self.getZ() - range,
                self.getX() + range,
                self.getY() + range,
                self.getZ() + range
        );

        List<Entity> entities = world.getEntities(self, box);

        Entity nearest = null;
        double nearestDistance = range;

        for (Entity entity : entities) {
            if (entity.equals(self)) {
                continue;
            }

            if (!matchesType(entity, includes)) {
                continue;
            }

            double distance = self.distanceTo(entity);
            if (distance < nearestDistance) {
                nearest = entity;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    private static boolean matchesType(Entity entity, List<String> types) {
        for (String type : types) {
            switch (type.toLowerCase()) {
                case "entity":
                    return true;
                case "livingentity":
                case "creature":
                    if (entity instanceof LivingEntity) {
                        return true;
                    }
                    break;
                case "player":
                    if (entity instanceof Player) {
                        return true;
                    }
                    break;
                default:
                    String entityId = entity.getType().toShortString();
                    if (entityId.equalsIgnoreCase(type) || 
                        entityId.equalsIgnoreCase("minecraft:" + type)) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
}
