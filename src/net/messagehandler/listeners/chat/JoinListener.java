package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class JoinListener implements Listener {
    private final MessageHandler plugin;
    public JoinListener(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoinMessage(PlayerJoinEvent e) {
        if(!plugin.getConfig().getBoolean("Join.Enable")) {
            e.setJoinMessage(null);
            return;
        }
        User user = new User(e.getPlayer());
        if(!user.getJoinTitle().equals("") || user.getJoinTitle() != null) {
            user.sendTitle(Utility.parseMessage(plugin, user.getPlayer(), user.getJoinTitle()));
        }
        if(user.hasData(DataType.JOIN)) {
            e.setJoinMessage(user.getData(DataType.JOIN));
            return;
        }
        e.setJoinMessage(Utility.parseMessage(plugin, e.getPlayer(), user.getJoinMessage()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFirstJoin(PlayerJoinEvent e) {
        FileConfiguration rules = Utility.getConfigByFile("settings/rules.yml", FileUtilType.DEFAULT);
        if(!rules.getBoolean("prompt")) return;
        User user = new User(e.getPlayer());
        if(!user.hasData()) {
            new BukkitRunnable() {
                public void run() {
                    List<String> list = Utility.colorizeList(rules.getStringList("Rules"), e.getPlayer());
                    Iterator<String> iterator = list.iterator();
                    while(iterator.hasNext()) {
                        user.sendMessage(iterator.next());
                        user.sendActionBarMessage("&8[&6&l!&8] &bType &6I Agree &bto agree to the rules before playing");
                    }
                    user.sendMessage(rules.getString("prompt message"));
                }
            }.runTaskLater(MessageHandler.getInstance(), 50L);
            DataManager.rulesPrompt.add(user.getUuid());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(!DataManager.rulesPrompt.contains(e.getPlayer().getUniqueId())) return;
        User user = new User(e.getPlayer());
        if(e.getMessage().equalsIgnoreCase("I agree")) {
            user.sendActionBarMessage("&8[&6&l!&8] &aYou can now enjoy playing");
            DataManager.rulesPrompt.remove(user.getUuid());
            e.setCancelled(true);
            user.setData(DataType.DATE_JOINED, Utility.getMonthToday() + " " + Utility.getDateToday());
            user.setData(DataType.NAME, user.getName());
            return;
        }else {
            user.sendActionBarMessage("&8[&6&l!&8] &cYou must agree to the rules first before playing");
            e.setCancelled(true);
        }
     }

     @EventHandler
     public void onPlayerInteract(PlayerInteractEvent e) {
        if(!DataManager.rulesPrompt.contains(e.getPlayer().getUniqueId())) return;
        User user = new User(e.getPlayer());
        e.setCancelled(true);
        user.sendActionBarMessage("&8[&6&l!&8] &cYou must agree to the rules first before playing");
     }

     @EventHandler
     public void onMove(PlayerMoveEvent e) {
         if(!DataManager.rulesPrompt.contains(e.getPlayer().getUniqueId())) return;
         User user = new User(e.getPlayer());
         e.setCancelled(true);
         user.sendActionBarMessage("&8[&6&l!&8] &cYou must agree to the rules first before playing");
     }

     @EventHandler(priority = EventPriority.LOWEST)
     public void onCommand(PlayerCommandPreprocessEvent e) {
         if(DataManager.rulesPrompt.contains(e.getPlayer().getUniqueId())) {
             User user = new User(e.getPlayer());
             e.setCancelled(true);
             user.sendActionBarMessage("&8[&6&l!&8] &cYou must agree to the rules first before playing");
         }
     }

    @EventHandler
    public void onJoinNameTag(PlayerJoinEvent e) {
        Utility.reloadNameTag(plugin);
        Utility.reloadTab(plugin);
    }


}
