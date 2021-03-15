package net.messagehandler.listeners.inventory.ticket.listeners;

import net.messagehandler.listeners.inventory.ticket.TicketMenu;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TicketCategoryClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lTicket Category"))) {
            return;
        }

        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getItemMeta() == null) return;
        String name = Utility.getItemName(e.getCurrentItem());
        Player player = (Player) e.getWhoClicked();
        User user = new User(player);

        if(Utility.stripColor(name).equals("Back")) {
            TicketMenu ticketMenu = new TicketMenu(user);
            ticketMenu.setup("player");
            ticketMenu.openInventory();
            e.setCancelled(true);
            return;
        }
        DataManager.ticketCategory.put(user.getUuid(), Utility.stripColor(name));
        e.setCancelled(true);
        user.getPlayer().closeInventory();
        String message = Utility.getPrefix() + ":&bSpecify your problem or type cancel if you do not want to proceed";
        user.sendTitle(message, 10, 36000, 20);
    }
}
