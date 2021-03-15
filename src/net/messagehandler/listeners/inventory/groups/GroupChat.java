package net.messagehandler.listeners.inventory.groups;

import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class GroupChat {
    private final User user;
    private final Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lGroup Chat"));

    public GroupChat(User user) {
        this.user = user;
    }

    public void setup() {

        inventory.setItem(0, Utility.createGUIItem("&3&lCreate",
                Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fCreate a group chat"), "", Utility.colorize("&5LEFT CLICK &7for public group"), Utility.colorize("&5SHIFT CLICK &7for private group")),
                Material.APPLE));
        inventory.setItem(1, Utility.createGUIItem("&3&lGroups",
                Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fList of all the group chats")), Material.GOLDEN_AXE));
        inventory.setItem(2, Utility.getPlayerHead(user.getName(), Utility.colorize("&3&lYour Groups"), Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fCheck group chats you are in"))));
        inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack",
                Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fGo back to the main menu")), Material.BARRIER));

    }

    public void open() {
        user.getPlayer().openInventory(inventory);
    }
}
