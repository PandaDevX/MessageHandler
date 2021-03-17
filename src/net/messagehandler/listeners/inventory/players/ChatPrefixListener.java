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

public class ChatPrefixListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lChat Customization"))) return;
        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;

        User user = new User((Player) e.getWhoClicked());
        if(e.getClick() == ClickType.RIGHT) {
            switch(Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
                case "Prefix":
                    e.setCancelled(true);
                    user.clearCustomPrefix();
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aSuccessfully cleared " + Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    break;
                case "Suffix":
                    e.setCancelled(true);
                    user.clearCustomSuffix();
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aSuccessfully cleared " + Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    break;
                case "Nickname":
                    e.setCancelled(true);
                    user.clearNickName();
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aSuccessfully cleared " + Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    break;
                default:
                    e.setCancelled(true);
                    break;
            }
        } else {
            switch(Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
                case "Prefix":
                case "Suffix":
                case "Nickname":
                    e.setCancelled(true);
                    user.getPlayer().closeInventory();
                    DataManager.customizationChat.put(user.getUuid(), Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    user.sendTitle("&e&lChat:&fType your " + Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()) + " or type &ccancel &fto cancel", 30, 36000, 20);
                    break;
                default:
                    e.setCancelled(true);
                    break;
            }
        }
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
         if(!DataManager.customizationChat.containsKey(e.getPlayer().getUniqueId())) return;
         User user = new User(e.getPlayer());
         if(e.getMessage().toLowerCase().contains("cancel")) {
             e.setCancelled(true);
             DataManager.customizationChat.remove(e.getPlayer().getUniqueId());
             user.sendTitle("&e&lChat:&aSuccessfully cancelled modifying chat");
             return;
         }
         String acceptable = e.getMessage().length() > 16 ? e.getMessage().substring(0, 16) : e.getMessage();
        ChatPrefix chatPrefix = new ChatPrefix(user);
         switch (DataManager.customizationChat.get(e.getPlayer().getUniqueId())) {
             case "Prefix":
                 user.setPrefix(acceptable);
                 e.setCancelled(true);
                 user.sendTitle(" : ", 1, 1, 1);
                 chatPrefix.setup();
                 chatPrefix.open();
                 break;
             case "Nickname":
                 user.setNickName(acceptable);
                 e.setCancelled(true);
                 user.sendTitle(" : ", 1, 1, 1);
                 chatPrefix.setup();
                 chatPrefix.open();
                 break;
             case "Suffix":
                 user.setSuffx(acceptable);
                 e.setCancelled(true);
                 user.sendTitle(" : ", 1, 1, 1);
                 chatPrefix.setup();
                 chatPrefix.open();
                 break;
         }
         DataManager.customizationChat.remove(e.getPlayer().getUniqueId());
    }
}
