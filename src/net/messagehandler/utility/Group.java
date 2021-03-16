package net.messagehandler.utility;

import net.messagehandler.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private final String name;
    FileUtil util = new FileUtil(MessageHandler.getInstance(), "groupchats.yml", FileUtilType.DATA);
    FileConfiguration config = util.get();
    public Group(String name) {
        this.name = name;
    }

    public int getGroupMembersOnline() {
        int count = 0;
        if(config.get("groups." + name + ".members") == null) {
            return 1;
        }
        for (String player : config.getStringList("groups." + name + ".members")) {
            Player online = Bukkit.getPlayerExact(player);
            if(online != null) {
                ++count;
            }
        }
        Player creator = Bukkit.getPlayerExact(config.getString("groups." + name + ".creator"));
        if(creator != null) {
            ++count;
        }
        return count;
    }

    public void disband() {
        config.set("groups." + name, null);
        util.save();
    }

    public List<String> getMembers() {
        List<String> list = new ArrayList<>();
        if(config.get("groups." + name + ".members") == null) {
            list.add(config.getString("groups." + name + ".creator"));
            return list;
        }
        list.add(config.getString("groups." + name + ".creator"));
        list.addAll(config.getStringList("groups." + name + ".members"));
        return list;
    }

    public List<String> getMembersRaw() {
        return config.getStringList("groups." + name + ".members");
    }

    public boolean add(String player) {
        if (getMembers().contains(player)) {
            return false;
        }
        List<String> list = getMembersRaw();
        list.add(player);
        config.set("groups." + name + ".members", list);
        util.save();
        return true;
    }

    public boolean remove(String player) {
        if(!getMembers().contains(player)) {
            return false;
        }
        if(getOwner().equals(player)) {
            setOwner(getMembersRaw().get(0));
            return true;
        }
        List<String> list = getMembersRaw();
        list.remove(player);
        config.set("groups." + name + ".members", list);
        util.save();
        return true;
    }

    public void sendMessage(String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            User user = new User(player);
            if(user.isInChannel()) {
                if(user.getChannel().equals(getName())) {
                    user.sendMessage(message);
                }
            }
        }
    }

    public void removeToChannel(String player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            User user = new User(online);
            if(user.isInChannel()) {
                if(user.getName().equalsIgnoreCase(player) && user.getChannel().equals(getName())) {
                    user.removeChannel();
                }
            }
        }
    }

    public void removeToChannel() {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            User user = new User(onlinePlayer);
            if(user.isInChannel()) {
                if(user.getChannel().equals(getName())) {
                    user.removeChannel();
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getTotalMembers() {
        int count = 1;
        if(config.get("groups." + name + ".members") == null) {
            return count;
        }
        count += config.getStringList("groups." + name + ".members").size();
        return count;
    }

    public String getType() {
        return config.getString("groups." + name + ".type");
    }

    public void setOwner(String player) {
        config.set("groups." + name + ".creator", player);
        util.save();
    }

    public String getOwner() {
        return config.getString("groups." + name + ".creator");
    }
}
