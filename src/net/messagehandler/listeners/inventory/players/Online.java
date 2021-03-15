package net.messagehandler.listeners.inventory.players;

import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.PaginatedList;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Online {

    private final User user;
    public int maxPage;
    public Online(User user) {
        this.user = user;
    }


    Inventory inventory = MainMenu.createInventory(6, "&6&lOnline");

    public void setup(int page) {
        List<ItemStack> list = new ArrayList<>();
        for(Player online : Bukkit.getOnlinePlayers()) {
            User onlineP = new User(online);
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            if(onlineP.hasNickName()) {
                lore.add(Utility.colorize("&7NAME: &f" + online.getName()));
            }
            String status = "";
            if(onlineP.isAFK()) {
                status = "AFK";
            }else if(onlineP.isHidden()){
                status = "Hidden";
            }else {
                status = "Online";
            }
            if(user.canSee(onlineP)) {
                lore.add(Utility.colorize("&7STATUS: &f" + status));
                lore.add(Utility.colorize("&7PING: " + Utility.getPing(onlineP.getPlayer())));
                list.add(Utility.getPlayerHead(online.getName(),
                        onlineP.hasNickName() ? Utility.colorize(onlineP.getNickName()) : Utility.colorize("&3&l" + online.getName()),
                        lore));
            }
        }
        PaginatedList paginatedList = new PaginatedList(list, 36);
        List<ItemStack> playerList = paginatedList.getListOfPage(page);
        this.maxPage = paginatedList.getMaxPage();
        inventory.setItem(4, Utility.createGUIItem("&3&lOnline Players", Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + maxPage)), Material.LAPIS_LAZULI));
        for(int i = 9; i < playerList.size() + 9; i++) {
            inventory.setItem(i, playerList.get(i-9));
        }

        if(maxPage > 1 && maxPage != page) {
            inventory.setItem(51, Utility.createGUIItem("&3&lNext", Material.ENDER_PEARL));
        }
        if(page > 1) {
            inventory.setItem(50, Utility.createGUIItem("&3&lPrev", Material.ENDER_PEARL));
        }
        inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
    }


    public void open() {
        user.getPlayer().openInventory(inventory);
    }

    public int getMaxPage() {
        return this.maxPage;
    }
}
