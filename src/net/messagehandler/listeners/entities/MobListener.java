package net.messagehandler.listeners.entities;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.messagehandler.MessageHandler;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.Utility;
import net.minecraft.server.v1_16_R3.MathHelper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MobListener implements Listener {
    BossBar bar;


    @EventHandler
    public void onIndicate(EntityDamageEvent e) {
        if(!MessageHandler.getInstance().getConfig().getBoolean("Indicator.Air")) return;

        if(e.getEntity() instanceof ArmorStand) {
            return;
        }
            if(e.getEntity() instanceof LivingEntity) {
                LivingEntity creature = (LivingEntity) e.getEntity();
                double dmg = e.getDamage();
                dmg = Math.ceil(dmg);
                String damage = String.valueOf(dmg);
                damage = damage.replace(".0", "");
                double randomX = Math.random();
                double randomY = Math.random();
                double randomZ = Math.random();
                randomX = -1+randomX;
                randomY = -1+randomY;
                randomZ = -1+randomZ;
                ArmorStand as = (ArmorStand) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation().add(randomX, randomY, randomZ), EntityType.ARMOR_STAND);
                as.setCustomName(Utility.colorize("&c" + damage));
                as.setCustomNameVisible(true);
                as.setVisible(false);
                as.setGravity(true);

                new BukkitRunnable() {
                    public void run() {
                        as.remove();
                    }
                }.runTaskLater(MessageHandler.getInstance(), 20L);
            }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!MessageHandler.getInstance().getConfig().getBoolean("Indicator.Bar")) return;
        Entity damaged = e.getEntity();
        Player player = null;
        if(e.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) e.getDamager();
            if(projectile.getShooter() instanceof Player) {
                player = (Player) projectile.getShooter();
            }
        }
        if(e.getDamager() instanceof Player) {
            player= (Player) e.getDamager();
        }
        if(player != null) {
            if(damaged.getType().name().equals("ARMOR_STAND")) return;
            if(player.getWorld() != damaged.getWorld()) return;
            if(damaged instanceof Player && damaged.hasMetadata("NPC")) return;
            if(damaged instanceof LivingEntity) {

                LivingEntity livingEntity = (LivingEntity) damaged;
                if(DataManager.entitySave.containsKey(livingEntity.getUniqueId())) {
                    bar = DataManager.entitySave.get(livingEntity.getUniqueId());
                    bar.setTitle(Utility.colorize("&b" + livingEntity.getName()));
                    if(!bar.getPlayers().contains(player)) {
                        bar.addPlayer(player);
                    }
                    return;
                }
                bar = createBossBar(MessageHandler.getInstance(), livingEntity, BarColor.RED, BarStyle.SOLID);
                bar.setTitle(Utility.colorize("&b" + livingEntity.getName()));
                DataManager.entitySave.put(livingEntity.getUniqueId(), bar);
                if(!bar.getPlayers().contains(player)) {
                    bar.addPlayer(player);
                }
            }
        }
    }

    public BossBar createBossBar(Plugin plugin, LivingEntity livingEntity, BarColor color, BarStyle style) {
        BossBar bossBar = plugin.getServer().createBossBar(Utility.colorize("&b" + livingEntity.getName()), color, style);
        new BukkitRunnable() {
            public void run() {
                if (!livingEntity.isDead()) {
                    bossBar.setProgress(livingEntity.getHealth() / livingEntity.getMaxHealth());
                    new BukkitRunnable() {
                        public void run() {
                            List<Player> players = bossBar.getPlayers();
                            for (Player player : players) {
                                bossBar.removePlayer(player);
                                DataManager.entitySave.remove(livingEntity.getUniqueId());
                            }
                            bossBar.setVisible(false);
                        }
                    }.runTaskLater(MessageHandler.getInstance(), 100L);
                } else {
                    List<Player> players = bossBar.getPlayers();
                    for (Player player : players) {
                        bossBar.removePlayer(player);
                        DataManager.entitySave.remove(livingEntity.getUniqueId());
                    }
                    bossBar.setVisible(false);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
        return bossBar;
    }

    @EventHandler
    public void onSpawnEntity(EntitySpawnEvent e) {
        if(!MessageHandler.getInstance().getConfig().getBoolean("Indicator.Custom Name")) return;
        if(!(e.getEntity() instanceof Mob)) return;
        Entity entity = e.getEntity();
        if(entity.getCustomName() != null) return;
        Mob mob = (Mob)e.getEntity();
        int health = (int)mob.getHealth();
        int maxHealth = (int)mob.getMaxHealth();
        entity.setCustomName(Utility.colorize("&c" + entity.getName() + " &a" + health + " / " + maxHealth));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent e) {
        if(!MessageHandler.getInstance().getConfig().getBoolean("Indicator.Custom Name")) return;
        if(!(e.getEntity() instanceof Mob)) return;
        Mob mob = (Mob)e.getEntity();
        if(!(mob instanceof LivingEntity)) return;
        String name = mob.getType().toString().toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        int health = (int) (mob.getHealth() - e.getDamage());
        health = Math.max(health, 0);
        int maxHealth = (int) mob.getMaxHealth();
        if(mob.isDead()) return;
        mob.setCustomName(Utility.colorize("&c" + name + " &a" + health + " / " + maxHealth));
    }
}
