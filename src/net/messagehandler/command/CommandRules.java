package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtil;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandRules implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandRules(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.rules")) {
            Utility.colorize(Utility.getPrefix() + "You are not permitted to do that");
            return true;
        }
        FileUtil rulesFile = new FileUtil(plugin, "settings/rules.yml", FileUtilType.DEFAULT);
        FileConfiguration config = rulesFile.get();
        List<String> rules = config.getStringList("Rules");
        for(String rule : rules) {
            User user = new User((Player) sender);
            user.sendMessage(rule);
        }
        return false;
    }
}
