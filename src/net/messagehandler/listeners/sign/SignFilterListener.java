package net.messagehandler.listeners.sign;

import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Arrays;
import java.util.List;

public class SignFilterListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFilter(SignChangeEvent e) {
        Player player = e.getPlayer();
        for(int i = 0; i < e.getLines().length; i++) {
            String line = e.getLine(i);

            if(player.hasPermission("messagehandler.sign.filtermsg") || player.hasPermission("messagehandler.sign.*")) {
                return;
            }
            List<String> words = Arrays.asList(line.split(" "));
            List<String> bannedWords = Utility.getConfigByFile("settings/words.yml", FileUtilType.DEFAULT).getStringList("words");
            if(Utility.sensor(words, bannedWords, new User(player))) {
                e.setCancelled(true);
                User user = new User(e.getPlayer());
                user.sendTitle("&2&lAnti Swear:&eYou cannot do that");
                return;
            }
        }
    }

    @EventHandler
    public void onAd(SignChangeEvent e) {
        Player player = e.getPlayer();
        for(int i = 0; i < e.getLines().length; i++) {
            String line = e.getLine(i);

            if (player.hasPermission("messagehandler.sign.filterad") || player.hasPermission("messagehandler.sign.*")) {
                return;
            }

            if(Utility.sensorAd(line, new User(player))) {
                e.setCancelled(true);
                User user = new User(e.getPlayer());
                user.sendTitle("&2&lAnti Advertise:&eYou cannot do that");
                return;
            }
        }
    }
}
