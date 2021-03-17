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

public class ChatPrefix {

    private Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lChat Customization"));
    private User user;
    public ChatPrefix(User user) {
        this.user = user;
    }

    public void setup() {

        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        }
        List<String> prefix = Arrays.asList("", "&7Current: &f" + user.getPrefix(), "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
        List<String> nick = Arrays.asList("", "&7Current: &f" + user.getPlayer().getDisplayName(), "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
        List<String> suffix = Arrays.asList("", "&7Current: &f" + user.getSuffix(), "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");

        inventory.setItem(1, Utility.createGUIItem("&3&lPrefix", Utility.colorizeList(prefix, user.getPlayer()), Material.PINK_STAINED_GLASS_PANE));
        inventory.setItem(2, Utility.createGUIItem("&3&lNickname", Utility.colorizeList(nick, user.getPlayer()), Material.PINK_STAINED_GLASS_PANE));
        inventory.setItem(3, Utility.createGUIItem("&3&lSuffix", Utility.colorizeList(suffix, user.getPlayer()), Material.PINK_STAINED_GLASS_PANE));
    }

    public void open() {
        new BukkitRunnable() {
            public void run() {
                user.getPlayer().openInventory(inventory);
            }
        }.runTaskLater(MessageHandler.getInstance(), 2L);
    }
}
