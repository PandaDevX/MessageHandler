package net.messagehandler.listeners.inventory.players;

import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Customization {
    private Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lCustomization"));
    private User user;
    public Customization(User user) {
        this.user = user;
    }

    public void setup() {
       for(int i = 0; i < inventory.getSize(); i++) {
           inventory.setItem(i, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
       }
       List<String> joinLore = Arrays.asList("", "&7Current: " + user.getJoinMessage(), "", "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
       inventory.setItem(0, Utility.createGUIItem("&3&lJoin Message", Utility.colorizeList(joinLore, user.getPlayer()), Material.GREEN_STAINED_GLASS_PANE));
       List<String> joinTLore = Arrays.asList("", "&7Current: " + user.getJoinTitle(), "&7Format: &fTitle:Subtitle", "", "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
       inventory.setItem(1, Utility.createGUIItem("&3&lJoin Title", Utility.colorizeList(joinTLore, user.getPlayer()), Material.YELLOW_STAINED_GLASS_PANE));
        List<String> nameTag = new ArrayList<>();
        if(user.hasCustomNameTag()) {
            String p = user.getNameTag()[0] != null ? user.getNameTag()[0] : "";
            String c = user.getNameTag()[1] != null ? user.getNameTag()[1] : "";
            c = c.toUpperCase();
            c = c.replace(" ", "_");
            String s = user.getNameTag()[2] != null ? user.getNameTag()[2] : "";
           if((c!= null) && (!c.equals(""))) {
               nameTag = Arrays.asList("", "&7Current: " + p + " &r" + ChatColor.valueOf(c) + user.getName() + " " + s, "", "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
           } else {
               nameTag = Arrays.asList("", "&7Current: " + p + " &r" + c + user.getName() + " " + s, "", "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
           }
       }
       if(nameTag.isEmpty()) {
           inventory.setItem(2, Utility.createGUIItem("&3&lNameTag", Utility.colorizeList(Arrays.asList("", "&7Current: &fNone", "", "&5CLICK &7To customize nametag"), user.getPlayer()), Material.LIGHT_BLUE_STAINED_GLASS_PANE));
       } else {
           inventory.setItem(2, Utility.createGUIItem("&3&lNameTag", Utility.colorizeList(nameTag, user.getPlayer()), Material.LIGHT_BLUE_STAINED_GLASS_PANE));
       }
       List<String> quitLore = Arrays.asList("", "&7Current: " + user.getLeaveMessage(), "", "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
       inventory.setItem(3, Utility.createGUIItem("&3&lQuit Message", Utility.colorizeList(quitLore, user.getPlayer()), Material.RED_STAINED_GLASS_PANE));
       List<String> chat = Arrays.asList("", "&7Current: " + user.getPrefix() + " &r" + user.getPlayer().getDisplayName() + "&r " + user.getSuffix(), "", "&5CLICK &7To change", "&5RIGHT CLICK &7To clear");
       inventory.setItem(4, Utility.createGUIItem("&3&lChat Format", Utility.colorizeList(chat, user.getPlayer()), Material.PINK_STAINED_GLASS_PANE));
    }

    public void open() {
        user.getPlayer().openInventory(inventory);
    }
}
