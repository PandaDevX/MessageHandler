package net.messagehandler.listeners.inventory.email;

import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class Mail {

    private final User user;
    public Mail(User user) {
        this.user = user;
    }

    Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lEmail"));

    public void setup() {
        inventory.setItem(0, Utility.createGUIItem("&3&lSend", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fSend an email")), Material.ENDER_CHEST));
        inventory.setItem(1, Utility.createGUIItem("&3&lInbox", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fCheck the inbox")), Material.TRIPWIRE_HOOK));
        inventory.setItem(2, Utility.createGUIItem("&3&lNotification", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fToggle email notification")), Material.BOW));
        inventory.setItem(4, Utility.createGUIItem("&c&lBack", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fGo back to main menu")), Material.BARRIER));
    }

    public void openEmail() {
        user.getPlayer().openInventory(inventory);
    }
}
