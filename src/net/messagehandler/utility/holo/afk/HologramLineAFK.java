package net.messagehandler.utility.holo.afk;

import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;

public class HologramLineAFK {

    private ArmorStand stand;

    public HologramLineAFK(User creator, Location l, String name) {
        stand = l.getWorld().spawn(l, ArmorStand.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setCustomName(Utility.colorize(name));
        stand.setCustomNameVisible(true);
        List<ArmorStand> list = new ArrayList();
        if(DataManager.indicator.containsKey(creator.getUuid())) {
            list = DataManager.indicator.get(creator.getUuid());
        }
        list.add(stand);
        DataManager.indicator.put(creator.getUuid(), list);
    }

    public void unregisterLine() {
        stand.remove();
    }

    public void update(String name) {
        if (!name.equals(Utility.stripColor(stand.getCustomName()))) {
            stand.setCustomName(Utility.colorize(name));
        }
    }

}