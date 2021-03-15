package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtil;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import net.messagehandler.utility.picture.PictureWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;

public class CommandMOTD implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandMOTD(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.motd")) {
            Utility.colorize(Utility.getPrefix() + "You are not permitted to do that");
            return true;
        }
        FileUtil motdFile = new FileUtil(plugin, "settings/motd.yml", FileUtilType.DEFAULT);
        FileConfiguration config = motdFile.get();
        if(!config.getBoolean("Motd.Enable")) {
            return true;
        }
        List<String> motds = config.getStringList("Motd.Message");
        if(!(sender instanceof Player)) {
            for(String motd : motds) {
                String message = Utility.parseMessage(MessageHandler.getInstance(), motd);
                sender.sendMessage(Utility.colorize(message));
            }
            return true;
        }
        Player player = (Player) sender;
        if(config.getBoolean("Picture.Enable")) {
            sendImage(player);
        }else {
            Iterator<String> iterator = motds.iterator();
            while(iterator.hasNext()) {
                User user = new User((Player) sender);
                user.sendMessage(iterator.next());
            }
        }

        return false;
    }


    private void sendImage(Player player) {
        PictureWrapper wrapper = new PictureWrapper(player);
        wrapper.runTaskAsynchronously(MessageHandler.getInstance());
    }
}
