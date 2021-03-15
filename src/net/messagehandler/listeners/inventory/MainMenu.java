package net.messagehandler.listeners.inventory;

import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class MainMenu {



    public static Inventory createInventory(int row, String name) {
         return Bukkit.createInventory(null, row * 9, Utility.colorize(name));
    }

}
