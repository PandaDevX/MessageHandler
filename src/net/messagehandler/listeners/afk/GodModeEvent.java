package net.messagehandler.listeners.afk;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class GodModeEvent implements Listener {
    private final MessageHandler plugin;
    public GodModeEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onDamagePlayer(EntityDamageByEntityEvent e) {
        if(!plugin.getConfig().getBoolean("AFK.God")) {
            return;
        }
        Entity damageDealer = e.getDamager();
        if(e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            User user = new User(victim);
            if(user.isAFK()) {
                e.setCancelled(true);
                if(damageDealer instanceof Player) {
                    Player dealer = (Player)damageDealer;
                    dealer.sendMessage(Utility.colorize(Utility.getPrefix() + "&cThat player is afk"));
                }
            }
        }
    }

    @EventHandler
    public void onDamageBlock(EntityDamageByBlockEvent e) {
        if(!plugin.getConfig().getBoolean("AFK.God")) {
            return;
        }
        if(e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            User user = new User(victim);
            if(user.isAFK()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!plugin.getConfig().getBoolean("AFK.God")) {
            return;
        }
        if(e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            User user = new User(victim);
            if(user.isAFK()) {
                e.setCancelled(true);
            }
        }
    }
}
