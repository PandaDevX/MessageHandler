package net.messagehandler.listeners.inventory.email;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.PaginatedList;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EmailLogo {

    private final User user;
    private int page;
    private int maxPage;
    public EmailLogo(User user) {
        this.user = user;
    }

    Inventory inventory = MainMenu.createInventory(6, "&6&lEmail Logo");

    public void setup(int page) {
        List<Material> itemStacks = Arrays.asList(Material.values());
        PaginatedList paginatedList = new PaginatedList(itemStacks, 36);
        inventory.setItem(4, Utility.createGUIItem("&3&lLogos", Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + paginatedList.getMaxPage())), Material.LAPIS_LAZULI));
        List<Material> itemStackList = paginatedList.getListOfPage(page);
        this.maxPage = paginatedList.getMaxPage();

        for(int i = 9; i < itemStackList.size() + 9; i++) {
            if(i == 9) {
                inventory.setItem(i, new ItemStack(itemStackList.get(i-8)));
                continue;
            }
            if(itemStackList.get(i-9) == Material.AIR || itemStackList.get(i-9) == null) continue;
            inventory.setItem(i, new ItemStack(itemStackList.get(i - 9)));
        }
        if(paginatedList.getMaxPage() > 1 && page != paginatedList.getMaxPage()) {
            inventory.setItem(51, Utility.createGUIItem("&3&lNext", Material.ENDER_PEARL));
        }
        if(page > 1) {
            inventory.setItem(50, Utility.createGUIItem("&3&lPrev", Material.ENDER_PEARL));
        }
        inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
    }

    public void openInventory() {
        user.getPlayer().openInventory(inventory);
    }

    public void openInventory(long tick) {
        new BukkitRunnable() {
            @Override
            public void run() {
                user.getPlayer().openInventory(inventory);
            }
        }.runTaskLater(MessageHandler.getInstance(), tick);
    }

    public int getMaxPage() {
        return maxPage;
    }
}
