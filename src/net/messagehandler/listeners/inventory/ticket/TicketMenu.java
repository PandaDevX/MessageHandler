package net.messagehandler.listeners.inventory.ticket;

import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Collections;

public class TicketMenu {

    private final User user;
    public TicketMenu(User user) {
        this.user = user;
    }
    Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lTicket Menu"));

    public void setup(String view) {
        if(view.equals("player")) {
            inventory.setItem(0, Utility.createGUIItem("&3&lCreate", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fCreate a new ticket")), Material.ARMOR_STAND));
            inventory.setItem(1, Utility.createGUIItem("&3&lTickets", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fShow all your tickets")), Material.STONE_SLAB));
            inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&3&lBack", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fGo back to main menu")), Material.BARRIER));
            return;
        }
        if(view.equals("admin")) {
            inventory.setItem(0, Utility.createGUIItem("&3&lOpen Tickets", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fCheck open tickets")), Material.BONE));
            inventory.setItem(1, Utility.createGUIItem("&3&lClosed Tickets", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fCheck closed tickets")), Material.CAULDRON));
            inventory.setItem(2, Utility.getPlayerHead(user.getName(),
                    Utility.colorize("&3&lAssigned Tickets"),
                    Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fCheck all tickets assigned to you"))));
            inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&3&lBack", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fGo back to main menu")), Material.BARRIER));
            return;
        }
        return;
    }

    public void openInventory() {
        user.getPlayer().openInventory(inventory);
    }
}
