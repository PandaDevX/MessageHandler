package net.messagehandler.listeners.book;

import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class BookColorListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onColor(PlayerEditBookEvent e) {
        Player player = e.getPlayer();
        BookMeta info = e.getNewBookMeta();
        List<String> newPage = new ArrayList<>();

        for(String page : info.getPages()) {
            String msg = page;

            msg = Utility.colorize(msg);
            if(player.hasPermission("messagehandler.book.color") || player.hasPermission("messagehandler.book.*")) {
                Utility.formatColor(msg);
            }
            if(player.hasPermission("messagehandler.book.format") || player.hasPermission("messagehandler.book.*")) {
                Utility.formatString(msg);
            }
            newPage.add(msg);
        }
        info.setPages(newPage);
        e.setNewBookMeta(info);
    }
}
