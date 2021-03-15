package net.messagehandler.listeners.chat.motd;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import net.messagehandler.utility.picture.PictureWrapper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class JoinSendMessageListener implements Listener {


    @EventHandler
    public void onJoinSendMessage(PlayerJoinEvent e) {
        FileConfiguration config = Utility.getConfigByFile("settings/motd.yml", FileUtilType.DEFAULT);
        if(config.getBoolean("Motd.Enable")) {
            new BukkitRunnable() {
                public void run () {
                    e.getPlayer().sendMessage(" ");
                    if(config.getBoolean("Picture.Enable")) {
                        sendImage(e.getPlayer());
                    }else {
                        Iterator<String> iterator = config.getStringList("Motd.Message").iterator();
                        User user = new User(e.getPlayer());
                        while(iterator.hasNext()) {
                            user.sendMessage(iterator.next());
                        }
                    }
                }
            }.runTaskLater(MessageHandler.getInstance(), 20L);
        }
    }

    private void sendImage(Player player) {
        PictureWrapper wrapper = new PictureWrapper(player);
        wrapper.runTaskAsynchronously(MessageHandler.getInstance());
    }


}
