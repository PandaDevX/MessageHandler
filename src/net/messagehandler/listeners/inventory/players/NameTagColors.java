package net.messagehandler.listeners.inventory.players;

import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NameTagColors {

    Inventory inventory = MainMenu.createInventory(6, "&6&lColors");
    private User user;
    public NameTagColors(User user) {
        this.user = user;
    }


    public void setup() {
        List<String> stringColors = new ArrayList<>();
        for(ChatColor chatColor : Arrays.asList(ChatColor.values())) {
            String string = chatColor.name().toLowerCase();
            string = string.substring(0, 1).toUpperCase() + string.substring(1);
            string = string.replace("_", " ");
            stringColors.add(string);
        }
        for(int i = 0; i < stringColors.size(); i++) {
            String color = stringColors.get(i);
            color = color.replace(" ", "_");
            color = color.toUpperCase();
            inventory.setItem(i, Utility.createGUIItem(ChatColor.valueOf(color) + stringColors.get(i), Material.GREEN_CONCRETE));
        }
    }

    public void open() {
        user.getPlayer().openInventory(inventory);
    }
}
