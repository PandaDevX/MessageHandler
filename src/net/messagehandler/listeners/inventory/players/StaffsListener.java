package net.messagehandler.listeners.inventory.players;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.MainPage;
import net.messagehandler.listeners.inventory.MenuClick;
import net.messagehandler.utility.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class StaffsListener implements Listener {
    FileUtil util = new FileUtil(MessageHandler.getInstance(), "settings/staff.yml", FileUtilType.DEFAULT);
    FileConfiguration config = util.get();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String invName = "&6&lStaffs";
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize(invName))) return;
        if(e.getCurrentItem() == null)return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        if(e.getClickedInventory() == null) return;

        int page = Integer.parseInt(Utility.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getLore().get(0).split(" ")[1]));


        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        e.setCancelled(true);

        if(name.equals(Utility.colorize(config.getString("Staffs.next.name")))) {
            if((MenuClick.staff.getMaxPage() - 1) == page) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            MenuClick.staff.setup(page + 1);
            return;
        }
        if(name.equals(Utility.colorize(config.getString("Staffs.prev.name")))) {
            if(page == 2) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            MenuClick.staff.setup(page - 1);
            return;
        }
        if(Utility.stripColor(name).equals("Back")) {
            MainPage mainPage = new MainPage();
            mainPage.setup((Player) e.getWhoClicked());
            e.getWhoClicked().openInventory(mainPage.getInventory());
        }
    }
}
