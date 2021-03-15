package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.*;
import java.util.logging.Level;

public class DingPlayerEvent implements Listener {

    private final MessageHandler plugin;
    public DingPlayerEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent e) {
        if(e.isCancelled()) return;
        if(!plugin.getConfig().getBoolean("Ping Player.Enable")) return;
        if(!e.getMessage().contains(plugin.getConfig().getString("Ping Player.Listener"))) {
            return;
        }
        if(!e.getPlayer().hasPermission("messagehandler.ping")) {
            return;
        }
        String message = e.getMessage();
        if(e.getRecipients().isEmpty()) return;
        for(Player player : e.getRecipients()) {
            String[] words = e.getMessage().split(" ");
               for(int i = 0; i < words.length; i++) {
                   if(!words[i].startsWith(plugin.getConfig().getString("Ping Player.Listener"))) {
                       continue;
                   }
                   if(words[i].equalsIgnoreCase("@all")) {
                       message = message.replace(words[i], Utility.colorize("&e&lAll"));
                       if(!player.getName().equals(e.getPlayer().getName())) {
                           player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 7f, 1f);
                           User user = new User(player);
                           user.sendTitle("&e" + e.getPlayer().getName() + ":&bMentioned You");
                       }
                   }
                   String playerName = words[i].substring(1);
                   if(playerName.equalsIgnoreCase(e.getPlayer().getName())) return;
                   Player target = Bukkit.getPlayer(playerName);
                   if(target == null) {
                       e.setMessage(e.getMessage());
                       return;
                   }
                   if(target.equals(player)) {
                       target.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 7f, 1f);
                       target.sendTitle(Utility.colorize("&e" + e.getPlayer().getName()), Utility.colorize("&bMentioned you"), 30, 10 ,30);
                       message = message.replace(words[i], Utility.colorize("&e&l" + target.getName() + "&r"));
                       e.getRecipients().remove(target);
                   }
               }
        }
        e.setMessage(message);
    }
}
