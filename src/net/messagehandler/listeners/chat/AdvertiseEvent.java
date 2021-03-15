package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvertiseEvent implements Listener {

    private final MessageHandler plugin;
    public AdvertiseEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void findAd(AsyncPlayerChatEvent e) {
        if(!plugin.getConfig().getBoolean("Anti Ad.Enable")) return;
        if(e.getPlayer().hasPermission("messagehandler.antiad.bypass")) return;
        if (Utility.sensorAd(e.getMessage(), new User(e.getPlayer()))) {
            if (e.getMessage().length() == 0)
                e.setCancelled(true);
            e.setCancelled(true);
            e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&bYou cannot do that"), 30, 20, 30);
        }
    }
}
