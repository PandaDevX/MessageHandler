package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class ChatRadiusEvent implements Listener {
    private final MessageHandler plugin;
    public ChatRadiusEvent (MessageHandler plugin) {
        this.plugin= plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if(new User(e.getPlayer()).isInChannel()) return;
        DataManager.globalChannel.remove(e.getPlayer().getUniqueId());
        DataManager.localChannel.remove(e.getPlayer().getUniqueId());
        DataManager.staffChannel.remove(e.getPlayer().getUniqueId());
        if(e.isCancelled()) return;
        if(!plugin.getConfig().getBoolean("Chat Channel.Radius Enable")) {
            return;
        }
        if(ChatColor.stripColor(e.getMessage()).charAt(0) == plugin.getConfig().getString("Chat Channel.StaffChat Listener").charAt(0)) {
            return;
        }
        int radius = plugin.getConfig().getInt("Chat Channel.Local Radius");
        String listener = plugin.getConfig().getString("Chat Channel.Global Listener");
        Set<Player> recipients = e.getRecipients();
        for(Player receiver : Bukkit.getOnlinePlayers()) {
            if(receiver.equals(e.getPlayer())) continue;
            recipients.remove(receiver);
            if(Utility.inRange(e.getPlayer(), receiver, radius)) {
                recipients.add(e.getPlayer());
                recipients.add(receiver);
            }
        }
        if(listener != null && ChatColor.stripColor(e.getMessage()).charAt(0) == listener.charAt(0)) {
            recipients.addAll(Bukkit.getOnlinePlayers());
            e.setMessage(e.getMessage().substring(1));
            DataManager.globalChannel.add(e.getPlayer().getUniqueId());
            return;
        }
        DataManager.localChannel.add(e.getPlayer().getUniqueId());
    }
}
