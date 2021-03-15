package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import net.messagehandler.utility.events.BroadcastHoloEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class CommandBroadcast implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandBroadcast(MessageHandler plugin) {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.broadcast")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }

        if(args.length == 0) {
            sender.sendMessage(Utility.colorize("&bCorrect Argument: &f/broadcast <message>"));
            return true;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        for(Player online : Bukkit.getOnlinePlayers()) {
            User user = new User(online);
            List<String> list = MessageHandler.getInstance().getConfig().getStringList("Broadcast");
            for(String msg : list) {
                user.sendMessage(msg.replace("{broadcast_message}", builder.toString()));
            }
            if(MessageHandler.getInstance().getConfig().getBoolean("BroadcastHolo")) {
                BroadcastHoloEvent broadcastHoloEvent = new BroadcastHoloEvent(sender.getName(), online);
                broadcastHoloEvent.setMessage(builder.toString());
                broadcastHoloEvent.setDeathInterval(5);
                Bukkit.getPluginManager().callEvent(broadcastHoloEvent);
            }
        }
        return false;
    }
}
