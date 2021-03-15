package net.messagehandler.listeners.inventory.email;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class EmailSend {

    private final User user;

    public EmailSend(User user) {
        this.user = user;
    }

    Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Utility.colorize("&6&lEmail Send"));

    public void setup() {
        String name = DataManager.mailRecipient.getOrDefault(user.getUuid(), "None");
        Material mat = DataManager.emailBlock.getOrDefault(user.getUuid(), Material.BLACK_WOOL);
        String message = DataManager.emailMessage.getOrDefault(user.getUuid(), Utility.colorize("&cMissing"));
        String subject = DataManager.emailSubject.getOrDefault(user.getUuid(), Utility.colorize("&cMissing"));
        ItemStack[] size = DataManager.emailAttachments.getOrDefault(user.getUuid(), null);
        String materialName = mat.toString().replace("_", " ");
        materialName = materialName.substring(0, 1).toUpperCase() + materialName.substring(1).toLowerCase();
        int x = 0;
        if(size != null) {
            for(int i = 0; i < size.length; i++) {
                if(size[i] == null || size[i].getType() == Material.AIR) {
                    continue;
                }
                x++;
            }
        }

        inventory.setItem(0, Utility.getPlayerHead(name, Utility.colorize("&3&lRecipient"), Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fSetup recipient")
        , Utility.colorize("&7Currently: &f" + name))));
        inventory.setItem(1, Utility.createGUIItem("&3&lLogo", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fSetup logo")
        , Utility.colorize("&7Currently: &f" + materialName)), mat));
        inventory.setItem(2, Utility.createGUIItem("&3&lMessage", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fSetup message"),
                Utility.colorize("&7Currently"), Utility.colorize("&7Subject: " + subject), Utility.colorize("&7Message: " + message),"", Utility.colorize("&5CLICK &7To input &6Message"),Utility.colorize("&5SHIFT CLICK &7To input &6Subject")), Material.PAPER));
        ItemStack itemStack = Utility.createGUIItem("&3&lAttachment", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fSetup attachment"),
                Utility.colorize("&7Current Items: &f" + x + " / 4")), Material.DIAMOND_SWORD);
        inventory.setItem(3, itemStack);
        inventory.setItem(4, Utility.createGUIItem("&3&lSubmit", Arrays.asList("", Utility.colorize("&7DESCRIPTION: &fClick to submit email"),"",
                Utility.colorize("&5SHIFT CLICK &7To cancel")), Material.BOOK));
    }


    public void openEmail(long tick) {
        new BukkitRunnable() {
            @Override
            public void run() {
                user.getPlayer().openInventory(inventory);
            }
        }.runTaskLater(MessageHandler.getInstance(), tick);
    }

    public void openEmail() {
        user.getPlayer().openInventory(inventory);
    }

}
