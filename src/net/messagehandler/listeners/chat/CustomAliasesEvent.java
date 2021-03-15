package net.messagehandler.listeners.chat;

import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Set;

public class CustomAliasesEvent implements Listener  {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        FileConfiguration config = Utility.getConfigByFile("settings/aliases.yml", FileUtilType.DEFAULT);
        if(!config.getBoolean("Enable")) return;
        String commandName = e.getMessage().toLowerCase().substring(1);
        if (!e.getPlayer().hasPermission("messagehandler.alias." + commandName) || !e.getPlayer().hasPermission("messagehandler.alias.*")) return;
        for(String command : config.getKeys(false)) {
            if(commandName.equals(command.toLowerCase())) {
                User user = new User(e.getPlayer());
                for (String msg : config.getStringList(command)) {
                    user.sendMessage(msg);
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onUnknownCommand(PlayerCommandPreprocessEvent e) {
        if(e.isCancelled()) return;
        FileConfiguration config = Utility.getConfigByFile("settings/aliases.yml", FileUtilType.DEFAULT);
        Set<String> names = config.getKeys(false);
        String command = e.getMessage().split(" ")[0].substring(1);
        if(names.contains(command.toLowerCase()) || command.toLowerCase().contains("info")) return;
        if(Bukkit.getServer().getHelpMap().getHelpTopic(e.getMessage().split(" ")[0]) == null) {
            User user = new User(e.getPlayer());
            e.setCancelled(true);
            user.sendTitle("&4&lError:&fUnknown command");
        }
    }

    @EventHandler
    public void onHelpCommand(PlayerCommandPreprocessEvent e) {
        FileConfiguration config = Utility.getConfigByFile("settings/help.yml", FileUtilType.DEFAULT);
        if(!config.getBoolean("Enable")) return;
        User user = new User(e.getPlayer());

        String message = e.getMessage();
        if(message.contains("/info") && message.contains(" ")) {
            String page = message.split(" ")[1];
            if(page.equals("default") || page.equals("unknown") || config.get("Help." + page) == null) {
                for(String msg : config.getStringList("Help.unknown")) {
                    e.setCancelled(true);
                    user.sendMessage(msg);
                }
            }
            if(!user.hasPermission("messagehandler.help." + page) || !user.hasPermission("messagehandler.help.*")) {
                e.setCancelled(true);
                user.sendMessage(Utility.getPrefix() + "You have no permission to do that");
                return;
            }
            for(String msg : config.getStringList("Help." + page)) {
                e.setCancelled(true);
                user.sendMessage(msg);
            }
            return;
        }
        if(message.equalsIgnoreCase("/info")) {
            for(String msg : config.getStringList("Help.default")) {
                if(!user.hasPermission("messagehandler.help.default") || !user.hasPermission("messagehandler.help.*")) {
                    e.setCancelled(true);
                    user.sendMessage(Utility.getPrefix() + "You have no permission to do that");
                    return;
                }
                e.setCancelled(true);
                user.sendMessage(msg);
            }
        }
    }
}
