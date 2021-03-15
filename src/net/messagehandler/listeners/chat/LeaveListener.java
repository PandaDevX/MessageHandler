package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.TabManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class LeaveListener implements Listener {

    private final MessageHandler plugin;
    public LeaveListener(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if(!plugin.getConfig().getBoolean("Leave.Enable")) {
            e.setQuitMessage(null);
            return;
        }
        User user = new User(e.getPlayer());
        e.setQuitMessage(Utility.parseMessage(MessageHandler.getInstance(), user.getLeaveMessage()));
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        e.setLeaveMessage(Utility.parseMessage(MessageHandler.getInstance(), plugin.getConfig().getString("Leave.Message.Kick")));
    }

    @EventHandler
    public void onLeaveListen(PlayerQuitEvent e) {
        Utility.reloadTab(plugin);
        Utility.reloadNameTag(plugin);
        User user = new User(e.getPlayer());
        user.removeChannel();
    }
}
