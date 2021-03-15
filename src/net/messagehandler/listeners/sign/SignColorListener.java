package net.messagehandler.listeners.sign;

import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignColorListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onSignColor(SignChangeEvent e) {
        Player player = e.getPlayer();
        for(int i = 0; i < e.getLines().length; i++) {
            String line = e.getLine(i);

            line = Utility.colorize(line);
            if(player.hasPermission("messagehandler.sign.color") || player.hasPermission("messagehandler.sign.*")) {
                Utility.formatColor(line);
            }
            if(player.hasPermission("messagehandler.sign.format") || player.hasPermission("messagehandler.sign.*")) {
                Utility.formatString(line);
            }
            e.setLine(i, line);
        }
    }
}
