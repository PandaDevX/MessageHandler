package net.messagehandler.listeners.inventory.groups;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.*;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Members {
    private final User user;
    private final Inventory inventory = MainMenu.createInventory(6, "&6&lMembers");
    private final String group;
    private int maxPage;

    public Members(User user, String group) {
        this.group = group;
        this.user = user;
    }

    public void setup(int page) {
        inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fBack to the main menu")), Material.BARRIER));

        List<ItemStack> list = new ArrayList<>();
        FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), "groupchats.yml", FileUtilType.DATA);
        FileConfiguration config = fileUtil.get();
        if(config.get("groups." + group + ".members") == null && config.get("groups." + group + ".creator") == null) {
            inventory.setItem(4, Utility.createGUIItem("&3&lGroups", Material.LAPIS_LAZULI));
            inventory.setItem(22, Utility.createGUIItem("&3&lEmpty", Material.BEACON));
        }
        List<String> members = new Group(group).getMembers();
        for(String member : members) {
            List<String> lore = new ArrayList<>();
            if(member.equals(new Group(group).getOwner())) {
                lore.add("");
                lore.add(Utility.colorize("&6&lOwner"));
                list.add(Utility.getPlayerHead(member, Utility.colorize("&3&l" + member), lore));
                continue;
            }
            lore.add("");
            lore.add(Utility.colorize("&6&lMember"));
            if(new Group(group).getOwner().equalsIgnoreCase(user.getName())) {
                lore.add("");
                lore.add(Utility.colorize("&5SHIFT CLICK &7To kick this member"));
            }
            list.add(Utility.getPlayerHead(member, Utility.colorize("&3&l" + member), lore));
        }
        PaginatedList paginatedList = new PaginatedList(list, 36);
        List<ItemStack> groupList = paginatedList.getListOfPage(page);
        this.maxPage = paginatedList.getMaxPage();
        inventory.setItem(4, Utility.createGUIItem("&3&l" + group, Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + maxPage)), Material.LAPIS_LAZULI));


        for(int i = 9; i < groupList.size() + 9; i++) {
            inventory.setItem(i, groupList.get(i-9));
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
