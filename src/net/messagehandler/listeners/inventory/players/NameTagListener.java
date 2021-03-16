package net.messagehandler.listeners.inventory.players;

import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NameTagListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lNameTag Customization"))) return;
        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        User user = new User((Player) e.getWhoClicked());

        if(e.getClick() == ClickType.RIGHT) {
            switch(Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
                case "Prefix":
                case "Suffix":
                    e.setCancelled(true);
                    user.setNameTag(Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).toLowerCase(), null);
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aSuccessfully cleared " + Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    break;
                case "Color":
                    /**
                     * do something here...
                     */
                    e.setCancelled(true);
                    break;
                default:
                    e.setCancelled(true);
                    break;
            }
        } else {
            switch(Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
                case "Prefix":
                case "Color":
                case "Suffix":
                    e.setCancelled(true);
                    DataManager.customizationNT.put(user.getUuid(), Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).toLowerCase());
                    user.getPlayer().closeInventory();
                    user.sendTitle("&e&lNameTag:&fType your " + Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()) + " or type &ccancel &fto cancel", 30, 36000, 20);
                    break;
                default:
                    e.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(!DataManager.customizationNT.containsKey(e.getPlayer().getUniqueId())) return;
        User user = new User(e.getPlayer());
        if(e.getMessage().toLowerCase().contains("cancel")) {
            e.setCancelled(true);
            DataManager.customizationNT.remove(user.getUuid());
            user.sendTitle("&e&lNameTag:&aSuccessfully cancelled");
            return;
        }
        e.setCancelled(true);
        String value = e.getMessage();
        String key = DataManager.customizationNT.get(user.getUuid());

        user.setNameTag(key, value);
        user.sendTitle(" : ");
        DataManager.customizationNT.remove(user.getUuid());
        NameTag nameTag = new NameTag(user);
        nameTag.setup();
        nameTag.open();
    }
}
