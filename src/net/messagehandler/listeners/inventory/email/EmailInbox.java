package net.messagehandler.listeners.inventory.email;

import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.PaginatedList;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EmailInbox {

    private User user;
    private int maxPage;
    Inventory inventory = MainMenu.createInventory(6, "&6&lInbox");
    public EmailInbox(User user) {
        this.user = user;
    }

    public void setup(int page) {
        List<ItemStack> itemStackList = new ArrayList<>();
        HashMap<String, String> hashMap = Utility.accessInboxMap(user);
        FileConfiguration playerData = Utility.getConfigByFile("playerdata.yml", FileUtilType.DATA);
        if(!hashMap.isEmpty()) {
            for (String emailID : playerData.getConfigurationSection(user.getUuid().toString() + ".mail").getKeys(false)) {
                ItemStack itemStack = new ItemStack(Material.valueOf(hashMap.get(emailID + " logo")));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(Utility.colorize("&6" + hashMap.get(emailID + " subject")));
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("&7From: &f" + hashMap.get(emailID + " sender"));
                lore.add("&7ID: &f" + emailID);
                lore.add("");
                lore.add("&5Click: &7To read");
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                itemMeta.setLore(Utility.colorizeList(lore, user.getPlayer()));
                itemStack.setItemMeta(itemMeta);
                itemStackList.add(itemStack);
            }
        }
        hashMap.clear();
        if(itemStackList.isEmpty()) {
            inventory.setItem(4, Utility.createGUIItem("&3&lInbox", Material.LAPIS_LAZULI));
            inventory.setItem(22, Utility.createGUIItem("&3&lEmpty", Material.BEACON));
            inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Arrays.asList("", Utility.colorize("&7Description: &fTo go back to menu")), Material.BARRIER));
            return;
        }

        PaginatedList paginatedList = new PaginatedList(itemStackList, 36);
        this.maxPage = paginatedList.getMaxPage();
        List<ItemStack> list = paginatedList.getListOfPage(page);
        inventory.setItem(4, Utility.createGUIItem("&3&lInbox", Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + maxPage)), Material.LAPIS_LAZULI));

        for(int i = 9; i < list.size() + 9; i++) {
            inventory.setItem(i, list.get(i-9));
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
        return this.maxPage;
    }
}
