package net.messagehandler.listeners.inventory.players;

import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class NameTagColorsListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lColors"))) return;
        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;

        String stringColor = Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
        stringColor = stringColor.replace(" ", "_");
        stringColor = stringColor.toUpperCase();
        ChatColor color = ChatColor.valueOf(stringColor);

        e.setCancelled(true);
        User user = new User((Player) e.getWhoClicked());
        NameTag nameTag = new NameTag(user);
        nameTag.setup(color);
        nameTag.open();

        user.setNameTag("color", color.name());
    }
}
