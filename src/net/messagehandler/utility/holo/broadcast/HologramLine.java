package net.messagehandler.utility.holo.broadcast;

import net.messagehandler.utility.Utility;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class HologramLine {

    private ArmorStand stand;

    public HologramLine(Location l, String name) {
        stand = l.getWorld().spawn(l, ArmorStand.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setCustomName(Utility.colorize(name));
        stand.setCustomNameVisible(true);
    }

    public void unregisterLine() {
        stand.remove();
    }

    public ArmorStand getStand() {
        return this.stand;
    }

    public void update(String name) {
        if (!name.equals(Utility.stripColor(stand.getCustomName()))) {
            stand.setCustomName(Utility.colorize(name));
        }
    }
}
