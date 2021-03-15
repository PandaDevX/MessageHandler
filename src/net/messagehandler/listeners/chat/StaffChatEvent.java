package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class StaffChatEvent implements Listener {
    private final MessageHandler plugin;

    public StaffChatEvent (MessageHandler plugin) {
        this.plugin= plugin;
    }

    @EventHandler
    public void onStaffChat(AsyncPlayerChatEvent e) {
        String staffChat = plugin.getConfig().getString("Chat Channel.StaffChat Listener") == null ? "#" : plugin.getConfig().getString("Chat Channel.StaffChat Listener");
        if(!plugin.getConfig().getBoolean("Chat Channel.StaffChat Enable")) return;
        if(!e.getMessage().startsWith(staffChat)) return;
        Set<Player> recipients = e.getRecipients();
        String permission = plugin.getConfig().getString("Chat Channel.StaffChat Permission");
        if(!e.getPlayer().hasPermission(permission)) {
            e.setMessage(e.getMessage());
            DataManager.localChannel.add(e.getPlayer().getUniqueId());
            return;
        }
        for(Player online : Bukkit.getOnlinePlayers()) {
            if(!online.hasPermission(permission)) {
                recipients.remove(online);
            }
        }

        if(ChatColor.stripColor(e.getMessage()).charAt(0) == staffChat.charAt(0)) {
            e.setMessage(e.getMessage().substring(1));
            DataManager.staffChannel.add(e.getPlayer().getUniqueId());
        }


    }
}
