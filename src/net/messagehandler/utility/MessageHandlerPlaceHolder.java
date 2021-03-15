package net.messagehandler.utility;

import com.sun.istack.internal.NotNull;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.messagehandler.MessageHandler;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class MessageHandlerPlaceHolder extends PlaceholderExpansion {

    private final MessageHandler plugin;

    public MessageHandlerPlaceHolder(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @Nonnull
    public String getIdentifier() {
        return "messagehandler";
    }

    @Nonnull
    public String getAuthor() {
        return "Raymart";
    }

    @Nonnull
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        String lower = identifier.toLowerCase();
        String str1;
        switch ((str1 = lower).hashCode()) {
            case -938578798:
                if (!str1.equals("channel"))
                    break;
                if (DataManager.localChannel.contains(player.getUniqueId()))
                    return plugin.getConfig().getString("Chat Channel.Local");
                if (DataManager.globalChannel.contains(player.getUniqueId()))
                    return plugin.getConfig().getString("Chat Channel.Global");
                if (DataManager.staffChannel.contains(player.getUniqueId()))
                    return plugin.getConfig().getString("Chat Channel.StaffChat");
                break;
        }
        return "";
    }
}
