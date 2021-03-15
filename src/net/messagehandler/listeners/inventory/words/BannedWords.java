package net.messagehandler.listeners.inventory.words;

import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.PaginatedList;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BannedWords {


    private final User user;
    public int maxPage;
    public BannedWords(User user) {
        this.user = user;
    }

    Inventory inventory = MainMenu.createInventory(6, "&6&lBanned Words");

    public void setup(int page) {
        List<String> words = Utility.getWords();

        List<ItemStack> items = new ArrayList<>();
        for(String word : words) {
            items.add(Utility.createGUIItem("&3&l" + word, Material.SLIME_BALL));
        }
        PaginatedList paginatedList = new PaginatedList(items, 36);
        this.maxPage = paginatedList.getMaxPage();
        inventory.setItem(4, Utility.createGUIItem("&3&lOnline Players", Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + maxPage)), Material.LAPIS_LAZULI));
        if(items.isEmpty()) {
            inventory.setItem(4, Utility.createGUIItem("&3&lGroups", Material.LAPIS_LAZULI));
            inventory.setItem(22, Utility.createGUIItem("&3&lEmpty", Material.BEACON));
        }
        for(int i = 9; i < items.size() + 9; i++) {
            inventory.setItem(i, items.get(i-9));
        }
        if(maxPage > 1 && maxPage != page) {
            inventory.setItem(51, Utility.createGUIItem("&3&lNext", Material.ENDER_PEARL));
        }
        if(page > 1) {
            inventory.setItem(50, Utility.createGUIItem("&3&lPrev", Material.ENDER_PEARL));
        }
        inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Arrays.asList("", Utility.colorize("&7Description: &fTo go back to menu")), Material.BARRIER));
    }

    public void open() {
        user.getPlayer().openInventory(inventory);
    }

    public int getMaxPage() {
        return maxPage;
    }
}
