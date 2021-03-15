package net.messagehandler.listeners.inventory.ticket;

import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class TicketCategory {

    private final User user;
    public TicketCategory(User user) {
        this.user=user;
    }
    FileConfiguration config = Utility.getConfigByFile("settings/ticket.yml", FileUtilType.DEFAULT);
    Inventory inventory = config.getConfigurationSection("categories").getKeys(false).size() >= 5 ?
            MainMenu.createInventory(config.getInt("inventory-row"), "&6&lTicket Category") :
            Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lTicket Category"));

    public void setup() {
        for(String keys : config.getConfigurationSection("categories").getKeys(false)) {

            List<String> lore = config.getStringList("categories." + keys + ".lore");
            for(int i = 0; i < lore.size(); i++) {
                lore.set(i, Utility.colorize(lore.get(i)));
            }
            inventory.setItem(config.getInt("categories." + keys + ".slot") - 1,
                    Utility.createGUIItem(config.getString("categories." + keys + ".name"),
                            lore,
                            Material.valueOf(config.getString("categories." + keys + ".type"))));
        }

        inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&3&lBack", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fGo back to main menu")), Material.BARRIER));
    }

    public void openInventory() {
        user.getPlayer().openInventory(inventory);
    }
}
