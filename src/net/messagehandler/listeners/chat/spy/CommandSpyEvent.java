package net.messagehandler.listeners.chat.spy;

import net.messagehandler.utility.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSpyEvent implements Listener  {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = new User(player);
            if (user.getPlayer().equals(e.getPlayer())) continue;
            if (user.isSpying("command")) {
                user.sendMessage("&e&lCommandSpy &6" + e.getPlayer().getName() + " > &b" + e.getMessage());
            }
        }
    }
}
