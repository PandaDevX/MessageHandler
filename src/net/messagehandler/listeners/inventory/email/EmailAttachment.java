package net.messagehandler.listeners.inventory.email;

import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class EmailAttachment {

    private final User user;

    public EmailAttachment(User user) {
        this.user = user;
    }

    Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lEmail Attachment"));

    public void setup() {

        ItemStack[] itemStacks = null;
        if(DataManager.emailAttachments.containsKey(user.getUuid())) {
            itemStacks = DataManager.emailAttachments.get(user.getUuid());
        }
        if(itemStacks != null) {
            for(int i = 0; i < itemStacks.length; i++) {
                inventory.setItem(i, itemStacks[i]);
            }
        }
        inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&3&lSubmit",
                Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fClick to save items")
                , "", Utility.colorize("&5SHIFT CLICK &7To cancel")), Material.TNT));
    }

    public void open() {
        user.getPlayer().openInventory(inventory);
    }
}
