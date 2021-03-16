package net.messagehandler.listeners.inventory.groups.listeners;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.groups.Groups;
import net.messagehandler.listeners.inventory.groups.Members;
import net.messagehandler.utility.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GroupClickListener implements Listener {
    public static Members members;


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        FileUtil util = new FileUtil(MessageHandler.getInstance(), "groupchats.yml", FileUtilType.DATA);
        FileConfiguration config = util.get();
            String key = Utility.stripColor(e.getView().getTitle());
            if (!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&3&l" + key))) return;
            if (e.getCurrentItem() == null) return;
            if (!e.getCurrentItem().hasItemMeta()) return;
            String name = Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            User user = new User((Player) e.getWhoClicked());
            switch (name) {
                case "Join":
                    if (config.getString("groups." + key + ".type").equals("Public")) {
                        Group group = new Group(key);
                        if(group.add(user.getName())) {
                            user.sendTitle("&2&lGroups:&eSuccessfully joined the group &c" + key);
                        }
                        break;
                    }
                    user.getPlayer().closeInventory();
                    user.sendTitle("&2&lGroups:&eGroup is private please type password to join or &ccancel &eto cancel", 10, 36000, 20);
                    DataManager.groupClicked.put(user.getUuid(), key);
                    break;
                case "Members":
                    members = new Members(user, key);
                    members.setup(1);
                    members.open();
                    DataManager.groupClicked.remove(user.getUuid());
                    break;
                case "Join Conversation":
                    user.setChannel(key);
                    user.getPlayer().closeInventory();
                    user.sendTitle("&2&lGroups:&eSuccessfully joined to &c" + key + " &echat channel");
                    user.sendMessage("&2&lGroups>> &eType: LEAVE " + key + " &eto leave the channel");
                    Utility.reloadNameTag(MessageHandler.getInstance());
                    DataManager.groupClicked.remove(user.getUuid());
                    break;
                case "Leave":
                    Group group = new Group(key);
                    user.getPlayer().closeInventory();
                    if(group.getOwner().equals(user.getName())) {
                        group.disband();
                        user.sendTitle("&2&lGroups:&eSuccessfully disband the channel &c" + key);
                    }else {
                        if(group.remove(user.getName())) {
                            user.sendTitle("&2&lGroups:&eSuccessfully left the channel &c" + key);
                        }
                    }
                    if(user.isInChannel() && user.getChannel().equals(key)) {
                        user.removeChannel();
                        Utility.reloadNameTag(MessageHandler.getInstance());
                    }
                    DataManager.groupClicked.remove(user.getUuid());
                    break;

                case "Disband":
                    Group ownedGroup = new Group(key);
                    ownedGroup.disband();
                    user.sendTitle("&2&lGroups:&eSuccessfully disband the channel &c" + key);
                    user.getPlayer().closeInventory();
                    break;
                case "Back":
                    Groups groups = new Groups(user);
                    groups.setup(1);
                    groups.open();
                    break;
            }
            e.setCancelled(true);
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(!DataManager.groupClicked.containsKey(e.getPlayer().getUniqueId())) return;
        if(DataManager.groupCreators.contains(e.getPlayer().getUniqueId())) return;
        FileUtil util = new FileUtil(MessageHandler.getInstance(), "groupchats.yml", FileUtilType.DATA);
        User user = new User(e.getPlayer());
        FileConfiguration config = util.get();
        if(!e.getMessage().equals("cancel")) {
            String msg = e.getMessage().split(" ")[0];
            String group = DataManager.groupClicked.get(e.getPlayer().getUniqueId());
            DataManager.groupClicked.remove(e.getPlayer().getUniqueId());
            if(msg.equals(config.getString("groups." + group + ".password"))) {
                Group gp = new Group(group);
                if(gp.add(user.getName())) {
                    user.sendTitle("&2&lGroups:&eSuccessfully joined to &c" + group + " &echat channel");
                    user.sendMessage("&2&lGroups>> &eType: LEAVE " + group + " &eto leave the channel");
                }
            } else {
                user.sendTitle("&2&lGroups:&eWrong password");
            }
            e.setCancelled(true);
        } else {
            e.setCancelled(true);
            user.sendTitle("&2&lGroups:&eSuccessfully cancel joining group");
        }
    }
}
