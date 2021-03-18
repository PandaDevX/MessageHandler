package net.messagehandler.listeners.inventory.ticket.listeners;

import net.messagehandler.listeners.inventory.MainPage;
import net.messagehandler.listeners.inventory.ticket.TicketCategory;
import net.messagehandler.listeners.inventory.ticket.Tickets;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TicketMenuClick implements Listener {
    public static Tickets tickets;

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lTicket Menu"))) {
            return;
        }

        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getItemMeta() == null) return;
        String name = Utility.getItemName(e.getCurrentItem());
        Player player = (Player) e.getWhoClicked();
        User user = new User(player);
        TicketCategory obj = new TicketCategory(user);
        tickets = new Tickets(user);


        switch (Utility.stripColor(name)) {
            case "Create":
                if(!user.hasPermission("messagehandler.ticketplayer") || !user.hasPermission("messagehandler.tickets.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                user.getPlayer().closeInventory();
                obj.setup();
                obj.openInventory();
                break;
            case "Open Tickets":
                if(!user.hasPermission("messagehandler.ticketadmin") || !user.hasPermission("messagehandler.tickets.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                tickets.setup(1, "open");
                tickets.openInventory();
                break;
            case "Closed Tickets":
                if(!user.hasPermission("messagehandler.ticketadmin") || !user.hasPermission("messagehandler.tickets.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                tickets.setup(1, "close");
                tickets.openInventory();
                break;
            case "Assigned Tickets":
                if(!user.hasPermission("messagehandler.ticketadmin") || !user.hasPermission("messagehandler.tickets.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                tickets.setup(1, "assign");
                tickets.openInventory();
                break;
            case "Tickets":
                if(!user.hasPermission("messagehandler.ticketplayer") || !user.hasPermission("messagehandler.tickets.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                tickets.setup(1);
                tickets.openInventory();
                break;
            case "Back":
                MainPage mainPage = new MainPage();
                mainPage.setup(user.getPlayer());
                user.getPlayer().openInventory(mainPage.getInventory());
                break;
        }
        e.setCancelled(true);
        user.getPlayer().updateInventory();
    }
}
