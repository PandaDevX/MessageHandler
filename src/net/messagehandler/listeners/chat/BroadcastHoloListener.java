package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.events.BroadcastHoloEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BroadcastHoloListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawnArmorStand(BroadcastHoloEvent e) {
        e.createHologram();
        new BukkitRunnable() {
            public void run() {
                if(e.isCancelled()) {
                    e.removeHologram();
                    cancel();
                }
                Vector playerDir = e.getRecipient().getLocation().getDirection();
                if(e.getSecondsBeforeDeath() < 0) {
                    e.removeHologram();
                    cancel();
                }
                e.getHologram().teleport(e.getRecipient().getLocation().add(playerDir.multiply(4)));
            }
        }.runTaskTimer(MessageHandler.getInstance(), 0L, 1L);
    }
}
