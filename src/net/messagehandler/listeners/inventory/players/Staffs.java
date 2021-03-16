package net.messagehandler.listeners.inventory.players;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Staffs {

    private final User user;
    FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), "settings/staff.yml", FileUtilType.DEFAULT);
    FileConfiguration config = fileUtil.get();
    public int maxPage;
    public Staffs(User user) {
        this.user = user;
    }


    Inventory inventory = MainMenu.createInventory(config.getInt("Staffs.row"), "&6&lStaffs");

    public void setup(int page) {
        List<ItemStack> list = new ArrayList<>();
        for(String staff : config.getConfigurationSection("Staffs.players").getKeys(false)) {
            List<String> lore = config.getStringList("Staffs.players." + staff + ".lore");
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, Utility.colorize(lore.get(i)));
            }
            list.add(Utility.getPlayerHead(staff, Utility.colorize(config.getString("Staffs.players." + staff + ".name")),
                    lore));
        }
        PaginatedList paginatedList = new PaginatedList(list, 36);
        List<ItemStack> playerList = paginatedList.getListOfPage(page);
        this.maxPage = paginatedList.getMaxPage();
        inventory.setItem(4, Utility.createGUIItem("&3&lStaff Players", Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + maxPage)), Material.LAPIS_LAZULI));
        for(int i = 9; i < playerList.size() + 9; i++) {
            inventory.setItem(i, playerList.get(i-9));
        }

        if(maxPage > 1 && maxPage != page) {
            inventory.setItem(config.getInt("Staffs.next.slot") - 1, Utility.createGUIItem(config.getString("Staffs.next.name"), Material.valueOf(config.getString("Staffs.next.material"))));
        }
        if(page > 1) {
            inventory.setItem(config.getInt("Staffs.prev.slot") - 1, Utility.createGUIItem(config.getString("Staffs.prev.name"), Material.valueOf(config.getString("Staffs.prev.material"))));
        }
    }


    public void open() {
        user.getPlayer().openInventory(inventory);
    }

    public int getMaxPage() {
        return maxPage;
    }
}
