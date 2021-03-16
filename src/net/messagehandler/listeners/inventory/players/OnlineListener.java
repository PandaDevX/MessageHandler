package net.messagehandler.listeners.inventory.players;

import net.messagehandler.listeners.inventory.MainPage;
import net.messagehandler.listeners.inventory.MenuClick;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class OnlineListener implements Listener {


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lOnline"))) return;

        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        if(e.getClickedInventory() == null) return;
        int page = Integer.parseInt(Utility.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getLore().get(0).split(" ")[1]));
        User user = new User((Player) e.getWhoClicked());
        String playerName = user.hasNickName() ? user.getNickName() : user.getName();
        String name = Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        e.setCancelled(true);
        if(name.equalsIgnoreCase("Next")) {
            if((MenuClick.online.getMaxPage() - 1) == page) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            MenuClick.online.setup(page+1);
            return;
        }
        if(name.equalsIgnoreCase("Prev")) {
            if(page == 2) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            MenuClick.online.setup(page-1);
            return;
        }
        if(name.equalsIgnoreCase("Back")) {
            MainPage mainPage = new MainPage();
            mainPage.setup(user.getPlayer());
            user.getPlayer().openInventory(mainPage.getInventory());
            return;
        }
        Player toShow = Bukkit.getPlayerExact(name);
        User toShowUser = new User(toShow);
        Preference preference = new Preference(toShowUser);
        preference.setup();
        preference.openInventory(user);
        return;
    }
}
