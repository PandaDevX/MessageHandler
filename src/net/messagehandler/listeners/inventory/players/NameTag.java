package net.messagehandler.listeners.inventory.players;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class NameTag {

    private Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lNameTag Customization"));
    private User user;
    public NameTag(User user) {
        this.user = user;
    }

    public void setup() {
        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        }
        String prefx = (user.getNameTag()[0] == null || user.getNameTag()[0].equals("")) ? "&fNone" : user.getNameTag()[0];
        List<String> prefix = Arrays.asList("", "&7Current: &f" + prefx, "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
        inventory.setItem(1, Utility.createGUIItem("&3&lPrefix", Utility.colorizeList(prefix, user.getPlayer()), Material.LIGHT_BLUE_STAINED_GLASS_PANE));
        String colr = (user.getNameTag()[1] == null || user.getNameTag()[1].equals("")) ? "&fNone" : user.getNameTag()[1];
        List<String> color = Arrays.asList("", "&7Current: &f" + colr, "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
        inventory.setItem(2, Utility.createGUIItem("&3&lColor", Utility.colorizeList(color, user.getPlayer()), Material.LIGHT_BLUE_STAINED_GLASS_PANE));
        String suffx = (user.getNameTag()[2] == null || user.getNameTag()[2].equals("")) ? "&fNone" : user.getNameTag()[2];
        List<String> suffix = Arrays.asList("", "&7Current: &f" + suffx, "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
        inventory.setItem(3, Utility.createGUIItem("&3&lSuffix", Utility.colorizeList(suffix, user.getPlayer()), Material.LIGHT_BLUE_STAINED_GLASS_PANE));
    }

    public void open() {
        new BukkitRunnable() {
            public void run() {
                user.getPlayer().openInventory(inventory);
            }
        }.runTaskLater(MessageHandler.getInstance(), 2L);
    }
}
