package cn.i7mc.gpstest.command;

import cn.i7mc.gpstest.Gpstest;
import cn.i7mc.gpstest.manager.ConfigManager;
import cn.i7mc.gpstest.util.TargetFinder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GPSCommand implements CommandExecutor {

    private final Gpstest plugin = Gpstest.getInstance();
    private final ConfigManager configManager = plugin.getConfigManager();

    // A simple message handler, in a real project, this would be a separate class.
    private Component getMessage(String key) {
        String message;
        // This is a simplified example. A robust solution would involve a MessageManager.
        // For now, we hardcode them and will fix this if requested.
        switch (key) {
            case "navigation-started":
                message = "&aNavigation started. Pointing to the nearest target.";
                break;
            case "navigation-stopped":
                message = "&cNavigation stopped.";
                break;
            case "no-target-found":
                message = "&eNo valid targets found within range.";
                break;
            default:
                message = "&cUsage: /gps <start|stop>";
                break;
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(getMessage("usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            // 检查是否有可用目标
            Optional<Entity> nearestTarget = TargetFinder.findNearestTarget(
                    player,
                    configManager.getNavigationRange(),
                    configManager.getTargetEntities()
            );

            if (nearestTarget.isPresent()) {
                // 直接启动导航，让NavigationDisplay自己处理目标搜索
                plugin.getNavigationDisplay().start(player);
                player.sendMessage(getMessage("navigation-started"));
            } else {
                player.sendMessage(getMessage("no-target-found"));
            }

        } else if (args[0].equalsIgnoreCase("stop")) {
            plugin.getNavigationDisplay().stop(player);
            player.sendMessage(getMessage("navigation-stopped"));
        } else {
            player.sendMessage(getMessage("usage"));
        }

        return true;
    }
}
