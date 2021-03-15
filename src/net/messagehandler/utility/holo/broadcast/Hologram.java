package net.messagehandler.utility.holo.broadcast;

import org.bukkit.Location;

import java.util.HashMap;

public class Hologram {

    private Location base;
    private HashMap<Integer, HologramLine> entries;

    public Hologram(Location base) {
        entries = new HashMap<>();
        this.base = base;
    }

    public void clear() {
        for (HologramLine line : entries.values()) {
            line.unregisterLine();
        }
        entries.clear();
    }

    public void teleport(Location location) {
        for(HologramLine line : entries.values()) {
            line.getStand().teleport(location.add(0, 0.28, 0));
        }
    }

    public void removeLine(int line) {
        HologramLine l = entries.remove(line);
        if (l != null) {
            l.unregisterLine();
        }
    }

    public void updateLine(int line, String name) {
        if (entries.get(line) != null) {
            entries.get(line).update(name);
        } else {
            entries.put(line, new HologramLine(base.add(0, 0.28, 0), name));
        }
    }
}
