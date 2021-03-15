package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtil;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.Utility;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class LogListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatLog(AsyncPlayerChatEvent e) {
        FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), Utility.getDateTodayLog() + ".txt", FileUtilType.CHATLOG);
        fileUtil.setup();
        FileConfiguration config = MessageHandler.getInstance().getConfig();

        if(!config.getBoolean("Logs.Chat")) {
            return;
        }
        Date time = Calendar.getInstance().getTime();

        try {
            FileWriter write = new FileWriter(fileUtil.getFile(), true);
            BufferedWriter writer = new BufferedWriter(write);
            writer.write("<<" + time + ">>" + e.getPlayer().getName() + " => " + ChatColor.stripColor(e.getMessage()));
            writer.newLine();
            write.flush();
            writer.close();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandLog(PlayerCommandPreprocessEvent e) {
        FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), Utility.getDateTodayLog() + ".txt", FileUtilType.COMMANDLOG);
        fileUtil.setup();
        FileConfiguration config = MessageHandler.getInstance().getConfig();

        if(!config.getBoolean("Logs.Command")) {
            return;
        }
        Date time = Calendar.getInstance().getTime();

        try {
            FileWriter write = new FileWriter(fileUtil.getFile(), true);
            BufferedWriter writer = new BufferedWriter(write);
            writer.write("<<" + time + ">>" + e.getPlayer().getName() + " => " + ChatColor.stripColor(e.getMessage()));
            writer.newLine();
            write.flush();
            writer.close();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
