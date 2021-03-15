package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtil;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.Utility;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {
    private final MessageHandler plugin;
    public ServerPingListener(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent e) {
        FileUtil motdFile = new FileUtil(plugin, "settings/motd.yml", FileUtilType.DEFAULT);
        FileConfiguration config = motdFile.get();
        boolean motdEnable = config.getBoolean("Server Motd.Enable");
        String motd = config.getString("Server Motd.Motd");
        if(motdEnable) {
            e.setMotd(Utility.parseMessage(MessageHandler.getInstance(), motd).replace("\\n", "\n"));
        }
    }
}
