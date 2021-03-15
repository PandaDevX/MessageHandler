package net.messagehandler.listeners.afk;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class VanishAFKEvent implements Listener {
    private final MessageHandler plugin;
    public VanishAFKEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVanish(PlayerJoinEvent e) {
        if(DataManager.afkPlayers.isEmpty()) return;
        User user = new User(e.getPlayer());
        if(user.hasPermission("messagehandler.vanish.admin")) return;
        for(UUID vanishedPlayers : DataManager.vanishedPlayers) {
            Player player = Bukkit.getPlayer(vanishedPlayers);
            e.getPlayer().hidePlayer(plugin, player);
        }
    }

    @EventHandler
    public void onVansishLeave(PlayerQuitEvent e) {
        if(DataManager.vanishedPlayers.isEmpty()) return;
        if(new User(e.getPlayer()).isHidden())
            new User(e.getPlayer()).setVanish(false);
    }
}
