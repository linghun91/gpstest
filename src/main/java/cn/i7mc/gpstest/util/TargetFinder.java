package cn.i7mc.gpstest.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TargetFinder {

    public static Optional<Entity> findNearestTarget(Player player, double range, List<String> targetEntityNames) {
        List<EntityType> targetTypes = targetEntityNames.stream()
                .map(name -> {
                    try {
                        return EntityType.valueOf(name.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return null; // Invalid entity type in config
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        Collection<Entity> nearbyEntities = player.getNearbyEntities(range, range, range);

        return nearbyEntities.stream()
                .filter(entity -> targetTypes.contains(entity.getType()))
                .min(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(player.getLocation())));
    }
}
