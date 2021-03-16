package net.messagehandler.listeners.inventory.players;

import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class Preference {

    private User user;
    private Inventory inventory;
    public Preference(User user) {
        this.user = user;
    }

    public void setup() {
        inventory = MainMenu.createInventory(3, "&6&lPreference: &f" + user.getName());

        for(int i = 0; i <= 9; i++) {
            inventory.setItem(i, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        }

        inventory.setItem(17, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        inventory.setItem(10, Utility.createGUIItem("&3&lChat", Arrays.asList("", Utility.colorize("&7Status: &7" + status(user.chat()))),Material.BOOK));
        inventory.setItem(12, Utility.createGUIItem("&3&lPrivate Message", Arrays.asList("", Utility.colorize("&7Status: &7" + status(user.pm()))),Material.BOOK));
        inventory.setItem(14, Utility.createGUIItem("&3&lEmail Notification", Arrays.asList("", Utility.colorize("&7Status: &7" + status(user.email()))),Material.BOOK));
        inventory.setItem(16, Utility.createGUIItem("&3&lTicket Notification", Arrays.asList("", Utility.colorize("&7Status: &7" + status(user.ticket()))),Material.BOOK));
        for(int i = 18; i < inventory.getSize(); i++) {
            inventory.setItem(i, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        }


    }

    public void openInventory() {
        user.getPlayer().openInventory(inventory);
    }

    public void openInventory(User anotherUser) {
        anotherUser.getPlayer().openInventory(inventory);
    }

    public String status(boolean status) {
        String stat = status ? "&aEnabled" : "&cDisabled";
        return stat;
    }
}
