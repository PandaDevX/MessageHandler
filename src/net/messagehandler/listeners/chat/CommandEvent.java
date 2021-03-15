package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.Utility;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandEvent implements Listener {
    private final MessageHandler plugin;
    public CommandEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        FileConfiguration config = Utility.getConfigByFile("settings/commands.yml", FileUtilType.DEFAULT);
        if(!config.getBoolean("Enable")) {
            return;
        }
        Player player = e.getPlayer();
        if(player.hasPermission("messagehandler.commandblocker.bypass")) {
            return;
        }
        String command = e.getMessage();
        List<String> blocked_commands = config.getStringList("Commands");

        for(String blocked : blocked_commands) {
            if(command.equalsIgnoreCase(blocked)) {
                player.sendMessage(plugin.getMessageFormatter().format("error.blocked-command")
                .replace("%command%", command)
                .replace("%name%", player.getName())
                .replace("%displayname%", player.getDisplayName()));
                e.setCancelled(true);
            }
        }
    }
}
