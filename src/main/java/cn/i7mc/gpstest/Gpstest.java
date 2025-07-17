package cn.i7mc.gpstest;

import org.bukkit.plugin.java.JavaPlugin;

import cn.i7mc.gpstest.command.GPSCommand;
import cn.i7mc.gpstest.display.NavigationDisplay;
import cn.i7mc.gpstest.manager.ConfigManager;

public final class Gpstest extends JavaPlugin {

    private ConfigManager configManager;
    private NavigationDisplay navigationDisplay;

    private static Gpstest instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        configManager = new ConfigManager(this);
        navigationDisplay = new NavigationDisplay(this, configManager);

        // Register command
        getCommand("gps").setExecutor(new GPSCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (navigationDisplay != null) {
            navigationDisplay.stopAll();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public NavigationDisplay getNavigationDisplay() {
        return navigationDisplay;
    }

    public static Gpstest getInstance() {
        return instance;
    }
}
