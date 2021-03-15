package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.Utility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class SpamEvent implements Listener {
    private final MessageHandler plugin;
    private final HashMap<UUID, String> messageBefore = new HashMap<>();
    private final HashMap<UUID, Long> chatCooldowns = new HashMap<>();
    private final HashMap<UUID, Long> commandCooldowns = new HashMap<>();

    public SpamEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onSpam(AsyncPlayerChatEvent e) {
        if(!plugin.getConfig().getBoolean("Anti Spam")) {
            return;
        }
        if(!plugin.getConfig().getBoolean("Anti Spam.Anti Chat Similar")) {
            return;
        }
        if(messageBefore.containsKey(e.getPlayer().getUniqueId()) && Utility.isMessageSimilarToBefore(messageBefore.get(e.getPlayer().getUniqueId()), e.getMessage().toLowerCase(), plugin.getConfig().getInt("Anti Spam.Similarity Percent"))) {
            e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&bYou cannot send the similar message"), 30, 20, 30);
            e.setCancelled(true);
            messageBefore.put(e.getPlayer().getUniqueId(), e.getMessage().toLowerCase());
            return;
        }
        messageBefore.put(e.getPlayer().getUniqueId(), e.getMessage().toLowerCase());
    }

    @EventHandler
    public void onChatCooldown(AsyncPlayerChatEvent e) {
        int chatSecond = plugin.getConfig().getInt("Anti Spam.Chat Cooldown.Seconds");
        if(e.isCancelled()) {
            return;
        }
        if(!plugin.getConfig().getBoolean("Anti Spam.Chat Cooldown")) {
            return;
        }
        if(!plugin.getConfig().getBoolean("Anti Spam.Chat Cooldown.Enable")) {
            return;
        }
        if(e.getPlayer().hasPermission("messagehandler.chatcooldown.bypass")) {
            return;
        }
        if(chatCooldowns.containsKey(e.getPlayer().getUniqueId())) {
            if(chatCooldowns.get(e.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                long timeLeft =  (chatCooldowns.get(e.getPlayer().getUniqueId()) - System.currentTimeMillis()) / 1000;
                String msg = plugin.getConfig().getString("Anti Spam.Chat Cooldown.Message").replace("%time_left%", String.valueOf(timeLeft));
                e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize(msg), 30, 20, 30);
                e.setCancelled(true);
                return;
            }
        }
        chatCooldowns.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + (chatSecond*1000));
    }

    @EventHandler
    public void onCommandCooldown(PlayerCommandPreprocessEvent e) {
        int chatSecond = plugin.getConfig().getInt("Anti Spam.Command Cooldown.Seconds");
        if(e.isCancelled()) {
            return;
        }
        if(!plugin.getConfig().getBoolean("Anti Spam.Command Cooldown.Enable")) {
            return;
        }
        if(e.getPlayer().hasPermission("messagehandler.cmdcooldown.bypass")) {
            return;
        }
        if(commandCooldowns.containsKey(e.getPlayer().getUniqueId())) {
            if(commandCooldowns.get(e.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                long timeLeft =  (commandCooldowns.get(e.getPlayer().getUniqueId()) - System.currentTimeMillis()) / 1000;
                String msg = plugin.getConfig().getString("Anti Spam.Command Cooldown.Message").replace("%time_left%", String.valueOf(timeLeft));
                e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize(msg), 30, 20, 30);
                e.setCancelled(true);
                return;
            }
        }
        commandCooldowns.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + (chatSecond*1000));
    }
}
