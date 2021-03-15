package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Locale;

public class DeathEvent implements Listener {

    private final MessageHandler plugin;
    public DeathEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        String deathMessage = "";
        Player player = e.getEntity();
        if (player.getKiller() != null) {
            deathMessage = plugin.getConfig().getString("Death Message.Murdered");
            deathMessage = deathMessage.replace("{killer}", player.getKiller().getName());
            deathMessage = Utility.parseMessage(plugin, player, deathMessage);
            e.setDeathMessage(deathMessage);
            User user = new User(player.getKiller());
            user.sendMessage(plugin.getConfig().getString("Death Message.Killer").replace("{victim}", player.getName()));
            User victim = new User(player);
            victim.sendMessage(plugin.getConfig().getString("Death Message.Victim").replace("{killer}", player.getName()));
            return;
        }
        EntityDamageEvent ede = player.getLastDamageCause();
        DamageCause cause = ede.getCause();
        String reason = "";
        if (cause == DamageCause.BLOCK_EXPLOSION) {
            reason = "Block explosion";
        }else if (cause == DamageCause.DRAGON_BREATH) {
            reason = "Dragon breath";
        }else if (cause == DamageCause.DROWNING) {
            reason = "Drowning";
        }else if (cause == DamageCause.ENTITY_EXPLOSION) {
            reason = "Entity explosion";
        }else if (cause == DamageCause.FALL) {
            reason = "Falling";
        }else if (cause == DamageCause.FALLING_BLOCK) {
            reason = "Falling Block";
        }else if (cause == DamageCause.LIGHTNING) {
            reason = "Lightning";
        }else if (cause == DamageCause.FIRE) {
            reason = "Fire";
        }else {
            reason = String.valueOf(cause).toLowerCase();
        }
        deathMessage = plugin.getConfig().getString("Death Message.Natural");
        deathMessage = deathMessage.replace("{reason}", reason);
        deathMessage = Utility.parseMessage(plugin, player, deathMessage);
        e.setDeathMessage(deathMessage);
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent e) {
        if(e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            EntityDamageEvent.DamageCause deathCause = player.getLastDamageCause().getCause();
            player.sendMessage(MessageHandler.getInstance().getMessageFormatter().format("death.death-cause")
            .replace("%cause%", deathCause.toString().toLowerCase()));
        }
    }
}
