package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.PaginatedList;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandHelp implements CommandExecutor {

    private MessageHandler plugin;

    public CommandHelp(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("messagehandler.help")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        FileConfiguration helpConfig = Utility.getConfigByFile("settings/help.yml", FileUtilType.DEFAULT);
        if(sender.isOp() || (!(sender instanceof Player))) {
            for(String help : helpConfig.getStringList("Help.op")) {
                sender.sendMessage(Utility.parseMessage(plugin, help));
            }
            return true;
        }
        User user = new User((Player) sender);
        int page = 1;
        List<String> help = new ArrayList<>();

        if(args.length == 0) {
            Set<String> helpSet = helpConfig.getConfigurationSection("Help").getKeys(false);
            for(String set : helpSet) {
                if(set.equals("default") || set.equals("unknown") || set.equals("op")) {
                    continue;
                }
                if(user.hasPermission("messagehandler.help." + set)) {
                    help.addAll(helpConfig.getStringList("Help." + set));
                }
            }
            if(help.isEmpty()) {
                help.addAll(helpConfig.getStringList("Help.default"));
            }
            user.sendMessage("&7Showing help");
            user.sendMessage("");
            PaginatedList paginatedList = new PaginatedList(help, helpConfig.getInt("Help.content"));
            List<String> finalList = paginatedList.getListOfPage(page);
            for(String msg : finalList) {
                user.sendMessage(msg);
            }
            user.sendMessage("");
            user.sendMessage("&7Page ( &a " + page + " / " + paginatedList.getMaxPage() + " )");
            return true;
        }
        if(args.length == 1) {
            if(Utility.isInt(args[0])) {
                page = Integer.parseInt(args[0]);
                if(help.isEmpty()) {
                    Set<String> helpSet = helpConfig.getConfigurationSection("Help").getKeys(false);
                    for(String set : helpSet) {
                        if(set.equals("default") || set.equals("unknown") || set.equals("op")) {
                            continue;
                        }
                        if(user.hasPermission("messagehandler.help." + set)) {
                            help.addAll(helpConfig.getStringList("Help." + set));
                        }
                    }
                    if(help.isEmpty()) {
                        help.addAll(helpConfig.getStringList("Help.default"));
                    }
                    PaginatedList paginatedList = new PaginatedList(help, helpConfig.getInt("Help.content"));
                    if(page > paginatedList.getMaxPage()) {
                        page = paginatedList.getMaxPage();
                    }
                    if(page < 1) {
                        page = 1;
                    }
                    user.sendMessage("&7Showing help");
                    user.sendMessage("");
                    List<String> finalList = paginatedList.getListOfPage(page);
                    for(String msg : finalList) {
                        user.sendMessage(msg);
                    }
                    user.sendMessage("");
                    user.sendMessage("&7Page ( &a " + page + " / " + paginatedList.getMaxPage() + " &7)");
                    return true;
                }
                PaginatedList paginatedList = new PaginatedList(help, helpConfig.getInt("Help.content"));
                if(page > paginatedList.getMaxPage()) {
                    page = paginatedList.getMaxPage();
                }
                if(page < 1) {
                    page = 1;
                }
                user.sendMessage("&7Showing help");
                user.sendMessage("");
                List<String> finalList = paginatedList.getListOfPage(page);
                for(String msg : finalList) {
                    user.sendMessage(msg);
                }
                user.sendMessage("");
                user.sendMessage("&7Page ( &a " + page + " / " + paginatedList.getMaxPage() + " &7)");
                return true;
            }
            String pageQuery = args[0];
            if(helpConfig.get("Help." + pageQuery) == null) {
                for(String msg : helpConfig.getStringList("Help.unknown")) {
                    user.sendMessage(msg);
                }
                return true;
            }
            if(!user.hasPermission("messagehandler.help." + pageQuery)) {
                user.sendMessage("&bYou cannot do that");
                return true;
            }
            help.addAll(helpConfig.getStringList("Help." + pageQuery));
            PaginatedList paginatedList = new PaginatedList(help, helpConfig.getInt("Help.content"));
            if(page > paginatedList.getMaxPage()) {
                page = paginatedList.getMaxPage();
            }
            if(page < 1) {
                page = 1;
            }
            user.sendMessage("&7Showing help");
            user.sendMessage("");
            List<String> finalList = paginatedList.getListOfPage(page);
            for(String msg : finalList) {
                user.sendMessage(msg);
            }
            user.sendMessage("");
            user.sendMessage("&7Page ( &a " + page + " / " + paginatedList.getMaxPage() + " &7)");
            return true;
        }
        for(String msg : helpConfig.getStringList("Help.unknown")) {
            user.sendMessage(msg);
        }
        return false;
    }
}
