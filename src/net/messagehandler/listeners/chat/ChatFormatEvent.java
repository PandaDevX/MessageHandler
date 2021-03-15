package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.HashMap;
import java.util.UUID;

public class ChatFormatEvent implements Listener {

    public static HashMap<UUID, String> channel = new HashMap<>();
    private final MessageHandler plugin;
    public ChatFormatEvent(MessageHandler plugin) {
        this.plugin = plugin;

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChatFormat(AsyncPlayerChatEvent e) {
        if(!plugin.getConfig().getBoolean("Chat Format.Enable"))
            return;
        String format = plugin.getConfig().getString("Chat Format.Format");
        format = Utility.parseMessage(plugin, e.getPlayer(), format);
        format = setupChannel(e.getPlayer(), format);
        format = Utility.colorize(format);
        format = format.replace("{message}", e.getMessage());
        format = format.replaceAll("%", "%%");
        format = replaceAllVariables(e.getPlayer(), format);
        e.setFormat(format);
        channel.remove(e.getPlayer().getUniqueId());
    }

    public String replaceAllVariables(Player player, String message) {
        message = colorFormat(message, player);
        return message;
    }

    public String colorFormat(String message, Player player) {
        if(player.hasPermission("messagehandler.chat.color")) {
            message = Utility.formatColor(message);
        }
        if(player.hasPermission("messagehandler.chat.format")) {
            message = Utility.formatString(message);
        }
        if(player.hasPermission("messagehandler.chat.*") || player.isOp()) {
            message = Utility.formatStringAll(message);
        }
        return message;
    }

    public static String setupChannel(Player player, String message) {
        FileConfiguration config = MessageHandler.getInstance().getConfig();
        String placeholders = message;
        User user = new User(player);
        if(user.isInChannel()) {
            placeholders = placeholders.replace("{channel}", config.getString("Chat Channel.Group").replace("{group}", new User(player).getChannel()));
            return Utility.colorize(placeholders);
        }
        if (config.getBoolean("Chat Channel.Radius Enable")) {
            if (DataManager.globalChannel.contains(player.getUniqueId()))
                placeholders = placeholders.replace("{channel}", config.getString("Chat Channel.Global"));
            if (DataManager.localChannel.contains(player.getUniqueId()))
                placeholders = placeholders.replace("{channel}", config.getString("Chat Channel.Local"));
            if (DataManager.staffChannel.contains(player.getUniqueId()))
                placeholders = placeholders.replace("{channel}", config.getString("Chat Channel.StaffChat"));
        } else {
            placeholders = placeholders.replace("{channel}", "");
        }
        return Utility.colorize(placeholders);
    }
}
