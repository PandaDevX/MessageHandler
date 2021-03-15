package net.messagehandler.listeners.afk;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class PlayerMoveEvent implements Listener {

    private final HashMap<UUID, Location> location = new HashMap<>();
    private final MessageHandler plugin;
    public PlayerMoveEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        User user = new User(e.getPlayer());
            if (e.getPlayer().getLocation().getBlock().isLiquid()) {
                if (user.isAFK()) {
                    e.setCancelled(true);
                    return;
                }
            }

        if(user.isAFK()) {
            if(didNotMove(e.getFrom(), e.getPlayer().getLocation())) return;
            user.setAFK(false);
            if(user.isHidden()) {
                user.setVanish(false);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&aYou are no longer afk"), 30, 20, 30);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Utility.colorize(Utility.getPrefix() + "&6" + e.getPlayer().getName() + " &bis no longer afk"));
                    }
                    Utility.reloadNameTag(plugin);
                }
            }.runTaskLater(MessageHandler.getInstance(), 40L);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        User user = new User(e.getPlayer());
        if(user.isAFK()) {
            user.sendActionBarMessage("&8[&6&l!&8] &cWARNING &8[&6&l!&8] &bYou cannot break block while not moving");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        User user = new User(e.getPlayer());
        if(user.isAFK()) {
            user.setAFK(false);
            if(user.isHidden()) {
                user.setVanish(false);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&aYou are no longer afk"), 30, 20, 30);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Utility.colorize(Utility.getPrefix() + "&6" + e.getPlayer().getName() + " &bis no longer afk"));
                    }
                    Utility.reloadNameTag(plugin);
                }
            }.runTaskLater(MessageHandler.getInstance(), 40L);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        User user = new User(e.getPlayer());
        if(user.isAFK()) {
            user.setAFK(false);
            if(user.isHidden()) {
                user.setVanish(false);
            }
            e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&aYou are no longer afk"), 30, 20, 30);
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Utility.colorize(Utility.getPrefix() + "&6" + e.getPlayer().getName() + " &bis no longer afk"));
            }
            Utility.reloadNameTag(plugin);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        User user = new User(e.getPlayer());
        if(e.getMessage().equalsIgnoreCase("/afk")) return;
        if(user.isAFK()) {
            user.setAFK(false);
            if(user.isHidden()) {
                user.setVanish(false);
            }

            e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&aYou are no longer afk"), 30, 20, 30);
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Utility.colorize(Utility.getPrefix() + "&6" + e.getPlayer().getName() + " &bis no longer afk"));
            }
            Utility.reloadNameTag(plugin);


        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        User user = new User(player);
        if(user.isAFK()) {
            user.setAFK(false);
            if(user.isHidden()) {
                user.setVanish(false);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&aYou are no longer afk"), 30, 20, 30);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Utility.colorize(Utility.getPrefix() + "&6" + e.getPlayer().getName() + " &bis no longer afk"));
                    }
                    Utility.reloadNameTag(plugin);
                }
            }.runTaskLater(MessageHandler.getInstance(), 40L);
        }
    }

    private boolean didNotMove(Location from, Location to) {
        return(from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ());
    }
}
