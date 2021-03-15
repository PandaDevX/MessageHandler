package net.messagehandler.utility;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class PAPIHook {

    public static String setPlaceholders(Player player, String message) {
        return message = PlaceholderAPI.setPlaceholders(player, message);
    }

    public static List<String> setPlaceholders(Player player, List<String> message) {
        return message = PlaceholderAPI.setPlaceholders(player, message);
    }
}
