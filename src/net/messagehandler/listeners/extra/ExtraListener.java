package net.messagehandler.listeners.extra;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import java.util.List;

public class ExtraListener implements Listener {
    FileConfiguration config = MessageHandler.getInstance().getConfig();
    @EventHandler
    public void onListen(PlayerGameModeChangeEvent e) {
        if(!config.getBoolean("Extras.Gamemode Change")) return;
        User user = new User(e.getPlayer());
        String gamemode = e.getNewGameMode().toString().toLowerCase();
        gamemode = gamemode.substring(0, 1).toUpperCase() + gamemode.substring(1);
        user.sendTitle("&2&lGamemode:&fChanged to &6" + gamemode);
    }

    @EventHandler
    public void onEnterBed(PlayerBedEnterEvent e) {
        if(!config.getBoolean("Extras.Bed")) return;
        if(e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            User user = new User(e.getPlayer());
            user.sendTitle("&2&lBed:&eGood Night");
        }
    }

    @EventHandler
    public void onLeaveBed(PlayerBedLeaveEvent e) {
        if(!config.getBoolean("Extras.Bed")) return;
        User user = new User(e.getPlayer());
        user.sendTitle("&2&lBed:&eGood Day");
    }

    @EventHandler
    public void onListenExtra(PlayerToggleFlightEvent e) {
        if(!config.getBoolean("Extras.Toggle Flight")) return;
        User user = new User(e.getPlayer());
        String status = e.isFlying() ? "Flying" : "Cancelled";
        user.sendTitle("&2&lFlight:&e" + status);

    }


    @EventHandler
    public void onFind(PlayerLevelChangeEvent e) {
        if(!config.getBoolean("Extras.Level Up")) return;
        User user = new User(e.getPlayer());
        if(e.getNewLevel() > e.getOldLevel() && (e.getNewLevel() - e.getOldLevel()) >= 1) {
            user.sendTitle("&2&lLevel Up:&eYou level up");
        }
    }

    @EventHandler
    public void onFindTwo(PlayerRecipeDiscoverEvent e) {
        if(!config.getBoolean("Extras.Recipe Discovery")) return;
        User user = new User(e.getPlayer());
        String recipe = e.getRecipe().getKey().replace("_", " ");
        recipe = recipe.substring(0, 1).toUpperCase() + recipe.substring(1);
        user.sendTitle("&2&lRecipes:&fYou learned &6" + recipe);
    }

    @EventHandler
    public void onTP(PlayerTeleportEvent e) {
        if(!config.getBoolean("Extras.Teleport")) return;
        if(e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN || e.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND) return;
        User user = new User(e.getPlayer());
        user.sendTitle("&2&lTeleport:&fSuccessfully teleported");
    }

    @EventHandler
    public void onChangeResourcePack(PlayerResourcePackStatusEvent e) {
        if(!config.getBoolean("Extras.Resource Pack")) return;
        User user = new User(e.getPlayer());
        user.sendTitle("&2&lResource Pack:&eResource pack changed");
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if(!config.getBoolean("Extras.Respawn")) return;
        User user = new User(e.getPlayer());
        user.sendMessage("&2&lRespawn:&eYou have been respawned");
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent e) {
        if(!config.getBoolean("Extras.Raids")) return;
        Player trigger = e.getPlayer();
        List<Raider> raiders = e.getRaid().getRaiders();
        if(raiders.isEmpty()) return;
        for(Raider raider : raiders) {
            if(raider instanceof Player) {
                Player player = (Player) raider;
                User user = new User(player);
                user.sendTitle("&2&lRaid:&6" + trigger.getName() + " &ftriggered a raid");
            }
        }
    }

    @EventHandler
    public void onRaidStop(RaidStopEvent e) {
        if(!config.getBoolean("Extras.Raids")) return;
        List<Raider> raiders = e.getRaid().getRaiders();
        if(raiders.isEmpty()) return;
        for(Raider raider : raiders) {
            if(raider instanceof Player) {
                Player player = (Player) raider;
                User user = new User(player);
                user.sendTitle("&2&lRaid:&eRaid has been stopped for > &6" + e.getReason());
            }
        }
    }

    @EventHandler
    public void onSpawnWave(RaidSpawnWaveEvent e) {
        if(!config.getBoolean("Extras.Raids")) return;
        if(e.getPatrolLeader() instanceof Player) {
            Player player = (Player) e.getPatrolLeader();
            User user = new User(player);
            user.sendTitle("&2&lRaid:&eSir the wave spawned, protect your team");
        }
    }

    @EventHandler
    public void onRaidFinish(RaidFinishEvent e) {
        if(!config.getBoolean("Extras.Raids")) return;
        if(!e.getWinners().isEmpty()) {
            for(Player winner : e.getWinners()) {
                User user = new User(winner);
                user.sendTitle("&2&lRaid:&eCongratulations for winning a raid");
            }
        }
    }

    @EventHandler
    public void weatherEvent(WeatherChangeEvent e) {
        if(!config.getBoolean("Extras.Weather Change")) return;
        if(e.toWeatherState()) {
            for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                User user = new User(player);
                user.sendTitle("&2&lWeather:&eIt started raining");
            }
        }else {
            for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                User user = new User(player);
                user.sendTitle("&2&lWeather:&eIt stopped raining");
            }
        }
    }

    @EventHandler
    public void weatherEventThunder(ThunderChangeEvent e) {
        if(!config.getBoolean("Extras.Weather Change")) return;
        if(e.toThunderState()) {
            for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                User user = new User(player);
                user.sendTitle("&2&lWeather:&eBeware of lightning");
            }
        }else {
            for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                User user = new User(player);
                user.sendTitle("&2&lWeather:&eLightning stopped");
            }
        }
    }

    @EventHandler
    public void createPortal(PortalCreateEvent e) {
        if(!config.getBoolean("Extras.Create Portal")) return;
        if(e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            User user = new User(player);
            user.sendTitle("&2&lPortal:&ePortal created successfully");
        }
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent e) {
        if(!config.getBoolean("Extras.Item Drop Name")) return;

        String name = "";
        Entity item = e.getItemDrop();
        String amount = e.getItemDrop().getItemStack().getAmount() > 1 ? " x" + e.getItemDrop().getItemStack().getAmount() : "";
        if(item.getCustomName() != null) {
            name = item.getCustomName() + amount;
        }
        Item itemInstance = e.getItemDrop();
        if(itemInstance.getItemStack().hasItemMeta()) {
            name = itemInstance.getItemStack().getItemMeta().getDisplayName() + amount;
        }
        item.setCustomName(name.equals("") ? Utility.colorize("&6" + item.getName() + amount) : name);
        item.setCustomNameVisible(true);
    }
}
