package net.messagehandler.listeners.inventory.email;

import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.Email;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EmailViewer implements Listener {

    private User user;

    Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lEmail Viewer"));

    public EmailViewer(User user) {
        this.user = user;
    }
    public EmailViewer() {

    }
    public void setup(Email email) {
        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        }
        ItemStack itemStack = new ItemStack(email.getLogo());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Utility.colorize("&6" + email.getSubject()));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&7Message: &f" + email.getMessageFirstHalf());
        if(!email.getMessageSecondHalf().equals("")) {
            lore.add("&f" + email.getMessageSecondHalf());
        }
        meta.setLore(Utility.colorizeList(lore, user.getPlayer()));
        itemStack.setItemMeta(meta);
        inventory.setItem(0, Utility.getPlayerHead(email.getSenderName(), Utility.colorize("&6" + email.getSenderName())));
        inventory.setItem(2, itemStack);
    }

    public void open() {
        user.getPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onView(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lEmail Viewer"))) return;
        if(e.getClickedInventory() == null) return;
        e.setCancelled(true);
    }
}
