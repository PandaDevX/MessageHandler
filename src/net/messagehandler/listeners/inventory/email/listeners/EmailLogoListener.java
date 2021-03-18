package net.messagehandler.listeners.inventory.email.listeners;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.email.EmailLogo;
import net.messagehandler.listeners.inventory.email.EmailSend;
import net.messagehandler.utility.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class EmailLogoListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lEmail Logo"))) {
            return;
        }
        if(e.getClickedInventory() == null) return;
        if(e.getCurrentItem() == null) return;
        int currentPage = Integer.parseInt(Utility.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getLore().get(0).split(" ")[1]));
        Player player = (Player) e.getWhoClicked();
        User user = new User(player);
        if(e.getCurrentItem().hasItemMeta()) {
            if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utility.colorize("&3&lNext"))) {
                if((EmailSendListener.logo.getMaxPage() - 1) == currentPage) {
                    e.getClickedInventory().setItem(51, null);
                }
                EmailSendListener.logo.setup(currentPage+1);
                e.setCancelled(true);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utility.colorize("&3&lPrev"))) {
                if(currentPage == 2) {
                    e.getClickedInventory().setItem(50, null);
                }
                EmailSendListener.logo.setup(currentPage - 1);
                e.setCancelled(true);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utility.colorize("&c&lBack"))) {
                user.getPlayer().closeInventory();
                EmailSend send = new EmailSend(user);
                send.setup();
                send.openEmail();
                e.setCancelled(true);
                return;
            }
        }
        e.setCancelled(true);
        DataManager.emailBlock.put(user.getUuid(), e.getCurrentItem().getType());
        EmailSend emailSend = new EmailSend(user);
        emailSend.setup();
        emailSend.openEmail();
    }
}
