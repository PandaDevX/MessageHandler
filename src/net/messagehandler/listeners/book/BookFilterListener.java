package net.messagehandler.listeners.book;

import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookFilterListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEditBook(PlayerEditBookEvent e) {
        Player player = e.getPlayer();
        BookMeta info = e.getNewBookMeta();

        List<String> pages = new ArrayList<>(info.getPages());
        if(player.hasPermission("messagehandler.book.filtermsg") || player.hasPermission("messagehandler.book.*")) {
            return;
        }

        for(int x = 0; x < pages.size(); x++) {
            String[] msg = pages.get(x).split(" ");

            for(int i = 0; i < msg.length; i++) {
                String finalMessage = msg[i].replaceAll("[\\W\\d]", "");

                if(Utility.sensor(Arrays.asList(finalMessage.split(" ")), Utility.getWords(), new User(player))) {
                    e.setCancelled(true);
                    User user = new User(e.getPlayer());
                    user.sendTitle("&2&lAnti Swear:&eYou cannot do that");
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEditBookAd(PlayerEditBookEvent e) {
        Player player = e.getPlayer();
        BookMeta info = e.getNewBookMeta();

        List<String> pages = new ArrayList<>(info.getPages());
        if(player.hasPermission("messagehandler.book.filterad") || player.hasPermission("messagehandler.book.*")) {
            return;
        }

        for(int x = 0; x < pages.size(); x++) {
            String[] msg = pages.get(x).split(" ");

            for(int i = 0; i < msg.length; i++) {
                String finalMessage = msg[i].replaceAll("[\\W\\d]", "");

                if(Utility.sensorAd(finalMessage, new User(e.getPlayer()))) {
                    e.setCancelled(true);
                    User user = new User(e.getPlayer());
                    user.sendTitle("&2&lAnti Advertise:&eYou cannot do that");
                    return;
                }
            }
        }
    }
}
