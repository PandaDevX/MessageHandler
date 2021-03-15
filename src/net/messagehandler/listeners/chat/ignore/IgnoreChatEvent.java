package net.messagehandler.listeners.chat.ignore;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtil;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class IgnoreChatEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        for(Player online : Bukkit.getOnlinePlayers()) {
            User onlinePlayer = new User(online);
            if(!onlinePlayer.getIgnorePlayers().isEmpty()
            && onlinePlayer.isIgnoring(new User(e.getPlayer()))) {
                e.getRecipients().remove(online);
            }
            if(!onlinePlayer.getIgnorePlayers().isEmpty() && onlinePlayer.isIgnoringAll()) {
                e.getRecipients().remove(online);
            }
        }
    }

    @EventHandler
    public void onToggledChat(AsyncPlayerChatEvent e) {
        for(Player online : Bukkit.getOnlinePlayers()) {
            User user = new User(online);
            FileUtil util = new FileUtil(MessageHandler.getInstance(), "playerdata.yml", FileUtilType.DATA);
            FileConfiguration config = util.get();
            if(config.get(user.getUuid().toString() + ".chat") == null) {
                config.set(user.getUuid().toString() + ".chat", true);
            }
            util.save();
            if(!user.chat()) {
                e.getRecipients().remove(user.getPlayer());
            }
        }
    }
}
