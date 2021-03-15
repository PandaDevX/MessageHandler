package net.messagehandler.utility.holo.afk;

import net.messagehandler.utility.User;
import org.bukkit.Location;

import java.util.HashMap;

public class HologramAFK {

    private Location base;
    private HashMap<Integer, HologramLineAFK> entries;
    private User creator;

    public HologramAFK(User creator, Location base) {
        entries = new HashMap<>();
        this.base = base;
        this.creator = creator;
    }

    public void reset() {
        for (HologramLineAFK line : entries.values()) {
            line.unregisterLine();
        }
        entries.clear();
    }

    public void removeLine(int line) {
        HologramLineAFK l = entries.remove(line);
        if (l != null) {
            l.unregisterLine();
        }
    }

    public void updateLine(int line, String name) {
        if (entries.get(line) != null) {
            entries.get(line).update(name);
        } else {
            entries.put(line, new HologramLineAFK(creator, base.add(0, 0.28, 0), name));
        }
    }

}