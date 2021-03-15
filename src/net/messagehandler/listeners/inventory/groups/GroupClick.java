package net.messagehandler.listeners.inventory.groups;

import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.Group;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class GroupClick {

    private final User user;
    private Inventory inventory;

    public GroupClick(User user) {
        this.user = user;
    }

    public void setup() {
        String group = DataManager.groupClicked.get(user.getUuid());
        inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&3&l" + DataManager.groupClicked.get(user.getUuid())));
        inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fBack to the menu")), Material.BARRIER));
        if(user.isInGroup(group)) {
            inventory.setItem(0, Utility.createGUIItem("&3&lJoin Conversation", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fJoin to the chat channel")), Material.WATER_BUCKET));
            inventory.setItem(1, Utility.createGUIItem("&3&lLeave", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fLeave the group chat")), Material.LAVA_BUCKET));
            inventory.setItem(2, Utility.getPlayerHead(new Group(group).getOwner(), Utility.colorize("&3&lMembers"), Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fCheck the members"))));
            if(user.isGroupLeader(group)) {
                inventory.setItem(3, Utility.createGUIItem("&3&lDisband", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fDisband the group")), Material.BEDROCK));
            }
            return;
        }
        inventory.setItem(0, Utility.createGUIItem("&3&lJoin", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fJoin the group chat")), Material.WATER_BUCKET));
        inventory.setItem(1, Utility.getPlayerHead(new Group(group).getOwner(), Utility.colorize("&3&lMembers"), Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fCheck the members"))));
    }

    public void open() {
        user.getPlayer().openInventory(inventory);
    }
}
