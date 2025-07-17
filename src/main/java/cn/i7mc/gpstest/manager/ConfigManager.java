package cn.i7mc.gpstest.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    private int navigationRange;
    private List<String> targetEntities;

    // Display animation settings
    private int updateInterval;
    private int interpolationDuration;
    private int interpolationDelay;
    private int teleportDuration;
    private double displayDistance;
    private float viewRange;
    private boolean autoStopWhenNoTarget;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        // Ensure the default config.yml is saved if it doesn't exist
        plugin.saveDefaultConfig();
        // Reload the configuration from disk
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        // Load navigation values
        navigationRange = config.getInt("navigation.range", 50);
        targetEntities = config.getStringList("navigation.target-entities");

        // Load display animation values
        updateInterval = config.getInt("display.update-interval", 3);
        interpolationDuration = config.getInt("display.interpolation-duration", 10);
        interpolationDelay = config.getInt("display.interpolation-delay", 0);
        teleportDuration = config.getInt("display.teleport-duration", 5);
        displayDistance = config.getDouble("display.display-distance", 2.0);
        viewRange = (float) config.getDouble("display.view-range", 64.0);
        autoStopWhenNoTarget = config.getBoolean("display.auto-stop-when-no-target", false);
    }

    public int getNavigationRange() {
        return navigationRange;
    }

    public List<String> getTargetEntities() {
        return targetEntities;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public int getInterpolationDuration() {
        return interpolationDuration;
    }

    public int getInterpolationDelay() {
        return interpolationDelay;
    }

    public int getTeleportDuration() {
        return teleportDuration;
    }

    public double getDisplayDistance() {
        return displayDistance;
    }

    public float getViewRange() {
        return viewRange;
    }

    public boolean isAutoStopWhenNoTarget() {
        return autoStopWhenNoTarget;
    }
}
