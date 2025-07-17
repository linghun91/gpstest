package cn.i7mc.gpstest.display;

import cn.i7mc.gpstest.manager.ConfigManager;
import cn.i7mc.gpstest.util.TargetFinder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Optional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NavigationDisplay {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final Map<UUID, ItemDisplay> activeDisplays = new HashMap<>();
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    public NavigationDisplay(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start(Player player) {
        stop(player); // Stop any existing navigation first

        // Calculate initial display location
        Location displayLocation = calculateDisplayLocation(player);

        // Create ItemDisplay with enhanced settings
        ItemDisplay itemDisplay = player.getWorld().spawn(displayLocation, ItemDisplay.class, display -> {
            display.setItemStack(new ItemStack(Material.IRON_SWORD));
            // 使用FIXED模式，这样我们可以完全控制旋转
            display.setBillboard(org.bukkit.entity.Display.Billboard.FIXED);

            // Configure smooth animations using Display API
            display.setInterpolationDuration(configManager.getInterpolationDuration());
            display.setInterpolationDelay(configManager.getInterpolationDelay());
            display.setTeleportDuration(configManager.getTeleportDuration());
            display.setViewRange(configManager.getViewRange());
        });

        activeDisplays.put(player.getUniqueId(), itemDisplay);

        // Create update task with configurable interval
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isValid()) {
                    stop(player);
                    return;
                }

                // 每次更新都重新搜索最近的目标
                Optional<Entity> nearestTarget = TargetFinder.findNearestTarget(
                    player,
                    configManager.getNavigationRange(),
                    configManager.getTargetEntities()
                );

                if (nearestTarget.isPresent()) {
                    // 更新位置和指向
                    updateDisplayPosition(itemDisplay, player);
                    updateDisplayRotation(itemDisplay, nearestTarget.get());
                } else {
                    // 如果没有找到目标，根据配置决定是否停止导航
                    if (configManager.isAutoStopWhenNoTarget()) {
                        stop(player);
                    }
                    // 如果配置为不自动停止，则保持剑的当前状态，只更新位置
                    else {
                        updateDisplayPosition(itemDisplay, player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, configManager.getUpdateInterval());

        activeTasks.put(player.getUniqueId(), task);
    }

    public void stop(Player player) {
        ItemDisplay display = activeDisplays.remove(player.getUniqueId());
        if (display != null) {
            display.remove();
        }

        BukkitTask task = activeTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public void stopAll() {
        activeTasks.values().forEach(BukkitTask::cancel);
        activeTasks.clear();
        activeDisplays.values().forEach(Entity::remove);
        activeDisplays.clear();
    }

    /**
     * 计算显示实体应该出现的位置
     */
    private Location calculateDisplayLocation(Player player) {
        Vector direction = player.getEyeLocation().getDirection();
        return player.getEyeLocation().add(direction.multiply(configManager.getDisplayDistance()));
    }

    /**
     * 更新显示实体的位置，使其始终在玩家前方
     */
    private void updateDisplayPosition(ItemDisplay display, Player player) {
        Location newLocation = calculateDisplayLocation(player);
        display.teleport(newLocation);
    }

    /**
     * 更新显示实体的旋转，使其指向目标
     * 使用Entity的setRotation方法，这是最直接的方式
     */
    private void updateDisplayRotation(ItemDisplay display, Entity target) {
        // 计算从显示实体到目标的方向向量
        Location displayLoc = display.getLocation();
        Location targetLoc = target.getLocation();

        // 计算方向向量
        Vector direction = targetLoc.toVector().subtract(displayLoc.toVector()).normalize();

        // 计算yaw角度 (水平旋转)
        double yaw = Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));

        // 计算pitch角度 (垂直旋转)
        double horizontalDistance = Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ());
        double pitch = Math.toDegrees(Math.atan2(-direction.getY(), horizontalDistance));

        // 设置实体旋转
        display.setRotation((float) yaw, (float) pitch);
    }
}
