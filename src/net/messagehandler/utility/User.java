package net.messagehandler.utility;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.messagehandler.MessageHandler;
import net.messagehandler.utility.holo.afk.HologramAFK;
import net.milkbowl.vault.Vault;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class User {

    private final UUID uuid;
    private final Player player;
    private final FileUtil playerFile = new FileUtil(MessageHandler.getInstance(), "playerdata.yml", FileUtilType.DATA);
    private final FileConfiguration config = playerFile.get();
    public User(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean hasData(DataType type) {
        switch(type) {
            case JOIN:
                return get(uuid.toString() + ".joinMessage") != null;
            case LEAVE:
                return get(uuid.toString() + ".leaveMessage") != null;
            case NAMETAG:
                return get(uuid.toString() + ".nameTag") != null;
            case CHAT_PREFIX:
                return get(uuid.toString() + ".chat_prefix") != null;
            case CHAT_SUFFIX:
                return get(uuid.toString() + ".chat_suffix") != null;
            case CHAT_TOGGLE:
                return get(uuid.toString() + ".chat_toggle") != null;
            case NAMETAG_COLOR:
                return get(uuid.toString() + ".nameTag_color") != null;
            case TAB_FOOTER:
                return get(uuid.toString() + ".tab_footer") != null;
            case TAB_HEADER:
                return get(uuid.toString() + ".tab_header") != null;
            case DATE_JOINED:
                return get(uuid.toString() + ".date_joined") != null;
            case NAME:
                return get(uuid.toString() + ".name") != null;
        }
        return false;
    }

    public String getData(DataType type) {
        switch (type) {
            case JOIN:
                return Utility.parseMessage(MessageHandler.getInstance(), player,  get(uuid.toString() + ".joinMessage"));
            case LEAVE:
                return Utility.parseMessage(MessageHandler.getInstance(), player, get(uuid.toString() + ".leaveMessage"));
            case NAMETAG:
                return get(uuid.toString() + ".nameTag");
            case NAMETAG_COLOR:
                return get(uuid.toString() + ".nameTag_color");
            case CHAT_PREFIX:
                return get(uuid.toString() + ".chat_prefix");
            case CHAT_SUFFIX:
                return get(uuid.toString() + ".chat_suffix");
            case CHAT_TOGGLE:
                return get(uuid.toString() + ".chat_toggle");
            case TAB_FOOTER:
                return Utility.parseMessage(MessageHandler.getInstance(), player, get(uuid.toString() + ".tab_footer"));
            case TAB_HEADER:
                return Utility.parseMessage(MessageHandler.getInstance(), player, get(uuid.toString() + ".tab_header"));
            case DATE_JOINED:
                return get(uuid.toString() + ".date_joined");
            case NAME:
                return get(uuid.toString() + ".name");
        }
        return null;
    }

    public void setData(DataType type, String message) {
        switch (type) {
            case JOIN:
                config.set(uuid.toString() + ".joinMessage", message);
                break;
            case LEAVE:
                config.set(uuid.toString() + ".leaveMessage", message);
                break;
            case NAMETAG:
                config.set(uuid.toString() + ".nameTag", message);
                break;
            case CHAT_PREFIX:
                config.set(uuid.toString() + ".chat_prefix", message);
                break;
            case CHAT_SUFFIX:
                config.set(uuid.toString() + ".chat_suffix", message);
                break;
            case CHAT_TOGGLE:
                config.set(uuid.toString() + ".chat_toggle", message);
                break;
            case TAB_FOOTER:
                config.set(uuid.toString() + ".tab_footer", message);
                break;
            case TAB_HEADER:
                config.set(uuid.toString() + ".tab_header", message);
                break;
            case NAMETAG_COLOR:
                config.set(uuid.toString() + ".nameTag_color", message.toUpperCase());
                break;
            case DATE_JOINED:
                config.set(uuid.toString() + ".date_joined", message);
                break;
            case NAME:
                config.set(uuid.toString() + ".name", message);
                break;
        }
        playerFile.save();
    }

    public String getName() {
        return player.getName();
    }

    public boolean isAFK() {
        if(DataManager.afkPlayers.isEmpty()) {
            return false;
        }
        return DataManager.afkPlayers.contains(uuid);
    }

    public void setAFK(boolean enable) {
        if(enable) {
            if(this.isAFK()) return;
            if(!MessageHandler.getInstance().getConfig().getBoolean("AFK.Vanish")) {
                List<String> list = new ArrayList<>();
                list.add("&7&l================ >> &6&lVvvV&7&l << ================");
                list.add("             &c&l" + getName() + " is currently AFK            ");
                list.add("&7&l================ >> &6&lVvvV&7&l << ================");
                createAFKIndicator(list);
            }
            DataManager.afkPlayers.add(uuid);
            Utility.reloadNameTag(MessageHandler.getInstance());
            return;
        }
        if(!this.isAFK())
            return;
        if(!MessageHandler.getInstance().getConfig().getBoolean("AFK.Vanish")) {
            removeAFKIndicator();
        }
        DataManager.afkPlayers.remove(uuid);
        Utility.reloadNameTag(MessageHandler.getInstance());
    }

    public boolean isHidden() {
        return !DataManager.vanishedPlayers.isEmpty() && DataManager.vanishedPlayers.contains(uuid);
    }

    public void setVanish(boolean enable) {
        if(enable) {
            if(isHidden()) return;
            DataManager.vanishedPlayers.add(uuid);
            if(hasPermission("messagehandler.vanish.effect"))
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 1, false));
            return;
        }
        if(!isHidden())
            return;
        DataManager.vanishedPlayers.remove(uuid);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public boolean hasPermission(String node) {
        return player.hasPermission(node);
    }

    public void sendMessage(String message) {
        Utility.sendMessage(MessageHandler.getInstance(), player, message);
    }

    public void sendTitle(String message, int fadeIn, int stay, int fadeOut) {
        Utility.sendTitle(MessageHandler.getInstance(), player, message, fadeIn, stay, fadeOut);
    }

    public void sendActionBarMessage(String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Utility.parseMessage(MessageHandler.getInstance(), getPlayer(), message)));
    }

    public void setNickName(String nickName) {
        config.set(this.getUuid().toString() + ".nick", nickName);
        playerFile.save();
        this.getPlayer().setDisplayName(Utility.colorize(nickName));
    }

    public void clearNickName() {
        config.set(this.getUuid().toString() + ".nick", null);
        playerFile.save();
        player.setDisplayName(getName());
    }

    public boolean searchPlayer(String name) {
        for(String uuid : config.getKeys(false)) {
            if(name.equalsIgnoreCase(config.getString(uuid + ".name"))) {
                return true;
            }
        }
        return false;
    }

    public Email composeEmail(String recipient, String subject, Material logo, String message, ItemStack[] attachments) {
        return new Email(this,  recipient, subject, logo, message, attachments);
    }

    public boolean isSpying(String spyType) {
        switch (spyType) {
            case "command":
                return !DataManager.commandSpy.isEmpty() && DataManager.commandSpy.contains(uuid);
            case "chat":
                return !DataManager.chatSpy.isEmpty() && DataManager.chatSpy.contains(uuid);
        }
        return false;
    }

    public void setSpy(String spyType, boolean enable) {
        switch (spyType) {
            case "command":
                if(enable) {
                    DataManager.commandSpy.add(uuid);
                    return;
                }
                DataManager.commandSpy.remove(uuid);
                break;
            case "chat":
                if(enable) {
                    DataManager.chatSpy.add(uuid);
                    return;
                }
                DataManager.chatSpy.remove(uuid);
                break;
        }
    }
    public void sendTitle(String message) {
        sendTitle(message, 40, 20, 40);
    }

    public boolean canSee(User target) {
        return getPlayer().canSee(target.getPlayer());
    }

    public void sendMessage(User target, String message) {
        if(target.isOffline()) {
            sendMessage("&cPlayer not found");
            return;
        }
        if(target.chat()) {
            sendTitle("&6&lError:&cYou cannot message that person");
            return;
        }
        if(target.isAFK()) {
            sendTitle("&6&lAFK:&bThat player is afk but sending message");
            target.sendMessage("&6&l" + getName() + " &3: &r" + message);
            sendMessage("&6&lto " + target.getName() + " >> &r" + message);
            DataManager.toReply.put(target.getUuid(), uuid);
            return;
        }
        if(target.isIgnoringAll() || target.isIgnoring(this)) {
            sendTitle("&6&lError:&bYou cannot send a message to that player");
            return;
        }
        if(!canSee(target) || target.isHidden()) {
            if (hasPermission("messagehandler.vanish.message")) {
                target.sendMessage("&6&l" + getName() + " &3: &r" + message);
                sendMessage("&6&lto " + target.getName() + " >> &r" + message);
                DataManager.toReply.put(target.getUuid(), uuid);
            } else {
                sendMessage("&cPlayer not found");
            }
        }
        target.sendMessage("&6&l" + getName() + " &3: &r" + message);
        sendMessage("&6&lto " + target.getName() + " >> &r" + message);
        DataManager.toReply.put(target.getUuid(), uuid);
        return;
    }

    public void reply(String message) {
        if(DataManager.toReply.containsKey(uuid)) {
            User target = new User(Bukkit.getPlayer(DataManager.toReply.get(uuid)));
            if(target.isOffline()) {
                sendTitle("&6&lError:&cPlayer not found");
                return;
            }
            if(!target.chat()) {
                sendTitle("&6&lError:&cYou cannot message that person");
                return;
            }
            if(target.isIgnoring(this) || target.isIgnoringAll()) {
                sendTitle("&6&lError:&bYou cannot send a message to that player");
                return;
            }
            sendMessage(target, message);
            return;
        }
        sendTitle("&6&lError:&bPlayer not found");
    }

    public void ignore(User target, boolean enable) {
        List<String> serialize = new ArrayList<>();
        if(enable) {
            if(getIgnorePlayers().isEmpty() || !getIgnorePlayers().contains(target.getUuid())) {
                List<UUID> uuidList = getIgnorePlayers();
                uuidList.add(target.getUuid());
                for(UUID id : uuidList) {
                    serialize.add(id.toString());
                }
                config.set(uuid.toString() + ".ignored_players", serialize);
                playerFile.save();
                return;
            }
            return;
        }
        if(!getIgnorePlayers().isEmpty() && getIgnorePlayers().contains(target.getUuid())) {
            List<UUID> uuidList = getIgnorePlayers();
            uuidList.remove(target.getUuid());
            if(!uuidList.isEmpty()) {
                for(UUID id : uuidList) {
                    serialize.add(id.toString());
                }
            }
            config.set(uuid.toString() + ".ignored_players", serialize);
            playerFile.save();
        }
    }
    public void ignoreAll(boolean enable) {
        List<String> list = config.getStringList(uuid.toString() + ".ignored_players");
        if(enable) {
            list.add("all");
        }else {
            list.remove("all");
        }
        config.set(uuid.toString() + ".ignored_players", list);
        playerFile.save();
    }
    public boolean ticket() {
        return config.getBoolean(uuid.toString() + ".ticket", true);
    }

    public void toggleTicketNotification() {
        if(ticket())
            config.set(uuid.toString() + ".ticket", false);
        else
            config.set(uuid.toString() + ".ticket", true);
        playerFile.save();

    }

    public boolean email() {
        return config.getBoolean(uuid.toString() + ".email", true);
    }

    public void toggleEmailNotification() {
        if(email())
            config.set(uuid.toString() + ".email", false);
        else
            config.set(uuid.toString() + ".email", true);
        playerFile.save();

    }

    public int getWarnPoints() {
        FileConfiguration data = playerFile.get();
        if(data.get(uuid.toString() + ".warn") == null) {
            data.set(uuid.toString() + ".warn", 0);
            return 0;
        }
        return data.getInt(uuid.toString() + ".warn");
    }

    public void warn(String reason, String admin) {
        if(!MessageHandler.getInstance().getConfig().getBoolean("Warn.Enable")) return;
        final String saidReason = reason.substring(0, 1).toUpperCase() + reason.substring(1).toLowerCase();;
        final int points = playerFile.get().get(uuid.toString() + ".warn") != null ?
                playerFile.get().getInt(uuid.toString() + ".warn") : 0;
        if(MessageHandler.getInstance().getConfig().get("Warn.Reasons") != null) {
            Set<String> reasons = MessageHandler.getInstance().getConfig().getConfigurationSection("Warn.Reasons").getKeys(false);
            reason = saidReason;
            if(!reasons.contains(reason)) {
                reason = "Other";
            }
            String action = MessageHandler.getInstance().getConfig().getString("Warn.Reasons." + reason + ".Action", "empty");
            String message = MessageHandler.getInstance().getConfig().getString("Warn.Reasons." + reason + ".Message", "empty");
            String broadcast = MessageHandler.getInstance().getConfig().getString("Warn.Reasons." + reason + ".Broadcast", "empty");
            if(!action.equals("empty")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("{warn_player}", getName()).replace("{warn_admin}", admin));
            }
            if(!message.equals("empty")) {
                sendActionBarMessage(message.replace("{warn_admin}", admin).replace("{warn_reason}", saidReason));
            }
            if(!broadcast.equals("empty")) {
                for(Player online : Bukkit.getOnlinePlayers()) {
                    User ol = new User(online);
                    ol.sendMessage(broadcast.replace("{warn_player}", getName()).replace("{warn_admin}", admin).replace("{warn_reason}", saidReason));
                }
            }
        }
        if(MessageHandler.getInstance().getConfig().getBoolean("Warn.Points.Enable")) {
            new BukkitRunnable() {
                public void run() {
                    for(String criterias : MessageHandler.getInstance().getConfig().getConfigurationSection("Warn.Points").getKeys(false)) {
                        if(!criterias.startsWith("Warn"))
                            continue;
                        int criteria = Integer.parseInt(criterias.split("_")[1]);
                        if((criteria - points) == 1) {
                            String action = MessageHandler.getInstance().getConfig().getString("Warn.Points.Warn_" + criteria + ".Action", "empty");
                            String message = MessageHandler.getInstance().getConfig().getString("Warn.Points.Warn_" + criteria + ".Message", "empty");
                            String broadcast = MessageHandler.getInstance().getConfig().getString("Warn.Points.Warn_" + criteria + ".Broadcast", "empty");
                            if(!action.equals("empty")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("{warn_player}", getName()).replace("{warn_admin}", admin));
                            }
                            if(!message.equals("empty")) {
                                sendActionBarMessage(message.replace("{warn_admin}", admin).replace("{warn_reason}", saidReason));
                            }
                            if(!broadcast.equals("empty")) {
                                for(Player online : Bukkit.getOnlinePlayers()) {
                                    User ol = new User(online);
                                    ol.sendMessage(broadcast.replace("{warn_player}", getName()).replace("{warn_admin}", admin).replace("{warn_reason}", saidReason));
                                }
                            }
                        }
                    }
                }
            }.runTaskLater(MessageHandler.getInstance(), 50L);
        }
        config.set(uuid.toString() + ".warn", (points+1));
        if(MessageHandler.getInstance().getConfig().getBoolean("Logs.Warn")) {
            FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), Utility.getDateTodayLog() + ".txt", FileUtilType.WARNLOG);
            fileUtil.setup();
            FileConfiguration config = MessageHandler.getInstance().getConfig();
            try {
                Date time = Calendar.getInstance().getTime();
                FileWriter write = new FileWriter(fileUtil.getFile(), true);
                BufferedWriter writer = new BufferedWriter(write);
                writer.write("<< " + time + " >> " + getName() + " has been warned for " + saidReason + " by " + admin);
                writer.newLine();
                write.flush();
                writer.close();
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        playerFile.save();
    }

    public boolean isIgnoring(User user) {
        return getIgnorePlayers().contains(user.getUuid());
    }

    public boolean isIgnoringAll() {
        List<String> ignoreList = config.getStringList(uuid.toString() + ".ignored_players");
        if(ignoreList.isEmpty()) {
            return false;
        }
        return ignoreList.contains("all");
    }

    public List<UUID> getIgnorePlayers() {
        List<UUID> finalList = new ArrayList<>();
        for(String idString : config.getStringList(uuid.toString() + ".ignored_players")) {
            UUID idConvert = UUID.fromString(idString);
            finalList.add(idConvert);
        }
        return finalList;
    }

    public void togglePrivateMessage() {
        if(pm()) {
            config.set(uuid.toString() + ".pm", false);
        } else {
            config.set(uuid.toString() + ".pm", true);
        }
        playerFile.save();
    }

    public void toggleChat() {
        if(chat()) {
            config.set(uuid.toString() + ".chat", false);
        } else {
            config.set(uuid.toString() + ".chat", true);
        }
        playerFile.save();
    }

    public List<String> getGroupsInvolved() {
        List<String> list = new ArrayList<>();
        FileConfiguration groupConfig = Utility.getConfigByFile("groupchats.yml", FileUtilType.DATA);
        for(String group : groupConfig.getConfigurationSection("groups").getKeys(false)) {
            if(groupConfig.getString("groups." + group + ".creator").equals(getName())) {
                list.add(group);
                continue;
            }
            if(groupConfig.get("groups." + group + ".members")!= null && !groupConfig.getStringList("groups." + group + ".members").isEmpty()) {
                if(groupConfig.getStringList("groups." + group + ".members").contains(getName())) {
                    list.add(group);
                }
            }
        }
        return list;
    }

    public boolean isInGroup(String groupName) {
        return getGroupsInvolved().contains(groupName);
    }

    public boolean isGroupLeader(String groupName) {
        FileConfiguration groupConfig = Utility.getConfigByFile("groupchats.yml", FileUtilType.DATA);
        return groupConfig.getString("groups." + groupName + ".creator").equals(getName());
    }

    public boolean createPublicGroup(String groupName) {
        FileUtil util = new FileUtil(MessageHandler.getInstance(), "groupchats.yml", FileUtilType.DATA);
        FileConfiguration groupConfig = util.get();
        if(groupConfig.get("groups." + groupName) != null) {
            return false;
        }
        groupConfig.set("groups." + groupName + ".type", "Public");
        groupConfig.set("groups." + groupName + ".creator", getName());
        util.save();
        return true;
    }

    public boolean createPrivate(String groupName, String password) {
        FileUtil util = new FileUtil(MessageHandler.getInstance(), "groupchats.yml", FileUtilType.DATA);
        FileConfiguration groupConfig = util.get();
        if(groupConfig.get("groups." + groupName) != null) {
            return false;
        }
        groupConfig.set("groups." + groupName + ".type", "Private");
        groupConfig.set("groups." + groupName + ".creator", getName());
        groupConfig.set("groups." + groupName + ".password", password);
        util.save();
        return true;
    }

    public boolean isInChannel() {
        return config.get(uuid.toString() + ".channel") != null;
    }

    public String getChannel() {
        return config.getString(uuid.toString() + ".channel");
    }

    public void setChannel(String channel) {
        config.set(uuid.toString()  + ".channel", channel);
        playerFile.save();
    }

    public void createAFKIndicator(List<String> indicator) {
        HologramAFK hologram = new HologramAFK(this, getPlayer().getLocation());
        for(int i = 0; i < indicator.size(); i++) {
            hologram.updateLine(i, indicator.get(i));
        }
        DataManager.hologram.put(uuid, hologram);
    }

    public void removeAFKIndicator() {
        HologramAFK hologram = null;
        List<ArmorStand> armorStands = new ArrayList<>();
        if(DataManager.indicator.containsKey(getUuid())) {
            armorStands = DataManager.indicator.get(getUuid());
        }
        if(armorStands.isEmpty()) return;
        for(ArmorStand armorStand : armorStands) {
            armorStand.remove();
        }
        if(DataManager.hologram.containsKey(uuid)) {
            hologram = DataManager.hologram.get(uuid);
            DataManager.hologram.remove(uuid);
        }
        if(hologram != null) {
            hologram.reset();
        }
    }

    public void removeChannel() {
        config.set(uuid.toString() + ".channel", null);
        playerFile.save();
    }

    public String getJoinMessage() {
        if(config.get(uuid.toString() + ".joinMessage") == null)
            return MessageHandler.getInstance().getConfig().getString("Join.Message.Default");
        return config.getString(uuid.toString() + ".joinMessage");
    }

    public String getJoinTitle() {
        if(config.get(uuid.toString() + ".joinTitle") == null)
            return MessageHandler.getInstance().getConfig().getString("Join.Message.Default Title");
        return config.getString(uuid.toString() + ".joinTitle");
    }

    public String getLeaveMessage() {
        if(config.get(uuid.toString() + ".leaveMessage") == null)
            return MessageHandler.getInstance().getConfig().getString("Leave.Message.Default");
        return config.getString(uuid.toString() + ".leaveMessage");
    }

    public boolean hasCustomPrefix() {
        return (config.get(uuid.toString() + ".prefix") != null);
    }

    public boolean hasCustomNameTag() {
        if(config.get(uuid.toString() + ".nametag") == null) {
            return false;
        }
        for(String nametag : getNameTag()) {
            if(!nametag.equals("")) {
                return true;
            }
        }
        return false;
    }

    public void setNameTag(String type, String value) {
        config.set(uuid.toString() + ".nametag." + type, value);
        playerFile.save();
        Utility.reloadNameTag(MessageHandler.getInstance());
    }

    public String[] getNameTag() {
        String[] nameTag = {
        config.getString(uuid.toString() + ".nametag.prefix", ""),
        config.getString(uuid.toString() + ".nametag.color", ""),
        config.getString(uuid.toString() + ".nametag.suffix", "") };
        return nameTag;
    }

    public boolean hasEmptyEmail() {
        return config.get(uuid.toString() + ".mail") == null;
    }

    public String getPrefix() {
        String group = VaultHook.getPlayerGroup(player);
        if(hasCustomPrefix()) {
            VaultHook.setPrefix(player, config.getString(uuid.toString() + ".prefix"));
            return VaultHook.getPlayerPrefix(player) != null ? VaultHook.getPlayerPrefix(player) : "";
        }
        if(group.equals("")) {
            VaultHook.setPrefix(player, MessageHandler.getInstance().getConfig().getString("Chat Format.Default.Prefix"));
            return VaultHook.getPlayerPrefix(player) != null ? VaultHook.getPlayerPrefix(player) : "";
        }
        if(MessageHandler.getInstance().getConfig().get("Chat Format.Groups." + group + ".Prefix") == null) {
            VaultHook.setPrefix(player, MessageHandler.getInstance().getConfig().getString("Chat Format.Default.Prefix"));
            return VaultHook.getPlayerPrefix(player) != null ? VaultHook.getPlayerPrefix(player) : "";
        }
        VaultHook.setPrefix(player, MessageHandler.getInstance().getConfig().getString("Chat Format.Groups." + group + ".Prefix"));
        return VaultHook.getPlayerPrefix(player) != null ? VaultHook.getPlayerPrefix(player) : "";
    }

    public void setPrefix(String prefix) {
        config.set(uuid.toString() + ".prefix", prefix);
        playerFile.save();
    }

    public void setSuffx(String suffix) {
        config.set(uuid.toString() + ".suffix", suffix);
        playerFile.save();
    }

    public boolean hasCustomSuffix() {
        return config.get(uuid.toString() + ".suffix") != null;
    }

    public String getSuffix() {
        String group = VaultHook.getPlayerGroup(player);
        if(hasCustomSuffix()) {
            VaultHook.setPrefix(player, config.getString(uuid.toString() + ".suffix"));
            return VaultHook.getPlayerPrefix(player) != null ? VaultHook.getPlayerPrefix(player) : "";
        }
        if(group.equals("")) {
            VaultHook.setSuffix(player, MessageHandler.getInstance().getConfig().getString("Chat Format.Default.Suffix"));
            return VaultHook.getPlayerSuffix(player) != null ? VaultHook.getPlayerSuffix(player) : "";
        }
        if(MessageHandler.getInstance().getConfig().get("Chat Format.Groups." + group + ".Suffix") == null) {
            VaultHook.setSuffix(player, MessageHandler.getInstance().getConfig().getString("Chat Format.Default.Suffix"));
            return VaultHook.getPlayerSuffix(player) != null ? VaultHook.getPlayerSuffix(player) : "";
        }
        VaultHook.setSuffix(player, MessageHandler.getInstance().getConfig().getString("Chat Format.Groups." + group + ".Suffix"));
        return VaultHook.getPlayerSuffix(player) != null ? VaultHook.getPlayerSuffix(player) : "";
    }

    public void setConfig(String path, Object object) {
        config.set(path, object);
        playerFile.save();
    }

    public FileConfiguration getConfig() {
        return playerFile.get();
    }

    public boolean isInGroup() {
        return !getGroupsInvolved().isEmpty();
    }

    public boolean isOffline() {
        return player == null;
    }

    public boolean pm() {
        return config.getBoolean(uuid.toString() + ".pm");
    }

    public boolean chat() {
        return config.getBoolean(uuid.toString() + ".chat");
    }

    public boolean hasNickName() {
        return config.get(uuid.toString() + ".nick") != null;
    }

    public String getNickName() {
        return config.getString(uuid.toString() + ".nick") == null ? player.getDisplayName() : config.getString(uuid.toString() + ".nick");
    }

    public boolean hasData() {
        return config.get(uuid.toString()) != null;
    }

    private String get(String path) {
        return config.getString(path);
    }

    public Friends getPlayerFriend() {
        return new Friends(this);
    }
}
