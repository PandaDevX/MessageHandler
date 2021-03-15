package net.messagehandler.listeners.inventory;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPage {

    private Inventory inventory;
    private MessageHandler plugin;

    public MainPage() {
    }

    public void setup(Player player) {
        inventory = MainMenu.createInventory(4, "&6&lMain Menu");

        for(int i = 0; i <= 9; i++) {
            inventory.setItem(i, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        }
        inventory.setItem(17, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        inventory.setItem(18, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        inventory.setItem(26, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        for(int i = 27; i < inventory.getSize(); i++) {
            inventory.setItem(i, Utility.createGUIItem(" ", Material.BLACK_STAINED_GLASS_PANE));
        }

        ItemStack itemStackGroupChat = createItem("GroupChat", "Click for group chat section", Material.COMMAND_BLOCK);
        inventory.setItem(10, itemStackGroupChat);

        ItemStack itemStackFriends = createItem("Friends", "Click for friends section", Material.APPLE);
        inventory.setItem(11, itemStackFriends);

        ItemStack itemStackCustom = createItem("Customization", "Click for customization of messages", Material.CACTUS);
        inventory.setItem(12, itemStackCustom);

        List<String> description = player.hasPermission("messagehandler.ticket.admin") ?
                Arrays.asList("", Utility.colorize("&5&lLEFT CLICK &7for ticket menu"), Utility.colorize("&5&lSHIFT CLICK &7for admin view")) : Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fClick for ticket manager"));
        ItemStack itemStackTicket = Utility.createGUIItem("&3&lTicket", description, Material.CARROT);
        inventory.setItem(13, itemStackTicket);

        ItemStack itemStackEmail = createItem("eMail", "Click for email manager", Material.WARPED_PLANKS);
        inventory.setItem(14, itemStackEmail);

        inventory.setItem(15, createItem("Staffs", "Click for staff lists", Material.BLAZE_ROD));

        ItemStack itemStackEmerald = createItem("Online", "Click for list of online players", Material.EMERALD);
        inventory.setItem(16, itemStackEmerald);

        ItemStack itemStackSpawner = createItem("Banned Words", "Click for the list of banned words", Material.SPAWNER);
        inventory.setItem(20, itemStackSpawner);

        List<String> lore = new ArrayList<>();
        String status = new User(player).isHidden() ? "&fInvisible" : "&aOnline";
        lore.add(Utility.colorize("&7Status: " + status));
        lore.add(Utility.colorize("&7Ping: ") + Utility.getPing(player) + " ms");
        inventory.setItem(19, Utility.getPlayerHead(player.getName(), Utility.colorize("&3&lYour Preference"), lore));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack createItem(String name, String desc) {
        ItemStack itemStackChat = new ItemStack(Material.PAPER);
        ItemMeta itemMetaChat = itemStackChat.getItemMeta();
        itemMetaChat.setDisplayName(Utility.colorize("&3&l" + name));
        itemMetaChat.setLore(Arrays.asList(desc));
        itemStackChat.setItemMeta(itemMetaChat);
        return itemStackChat;
    }

    public ItemStack createItem(String name, String desc, Material material) {
        ItemStack itemStackChat = new ItemStack(material);
        ItemMeta itemMetaChat = itemStackChat.getItemMeta();

        itemMetaChat.setDisplayName(Utility.colorize("&3&l" + name));
        itemMetaChat.setLore(Arrays.asList("", Utility.colorize("&7DESCRIPTION: &f" + desc)));
        itemStackChat.setItemMeta(itemMetaChat);
        return itemStackChat;
    }
}
