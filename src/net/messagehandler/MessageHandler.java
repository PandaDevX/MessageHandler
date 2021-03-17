package net.messagehandler;

import net.messagehandler.command.*;
import net.messagehandler.command.gui.*;
import net.messagehandler.listeners.anvil.AnvilColorListener;
import net.messagehandler.listeners.anvil.AnvilFilterListener;
import net.messagehandler.listeners.book.BookColorListener;
import net.messagehandler.listeners.book.BookFilterListener;
import net.messagehandler.listeners.chat.*;
import net.messagehandler.listeners.chat.motd.JoinSendMessageListener;
import net.messagehandler.listeners.extra.ExtraListener;
import net.messagehandler.listeners.inventory.MenuClick;
import net.messagehandler.listeners.inventory.email.EmailViewer;
import net.messagehandler.listeners.inventory.email.listeners.*;
import net.messagehandler.listeners.inventory.groups.listeners.GroupChatClick;
import net.messagehandler.listeners.inventory.groups.listeners.GroupClickListener;
import net.messagehandler.listeners.inventory.groups.listeners.GroupsClick;
import net.messagehandler.listeners.inventory.groups.listeners.MemberClick;
import net.messagehandler.listeners.inventory.players.*;
import net.messagehandler.listeners.inventory.ticket.listeners.*;
import net.messagehandler.listeners.afk.GodModeEvent;
import net.messagehandler.listeners.afk.PlayerMoveEvent;
import net.messagehandler.listeners.afk.VanishAFKEvent;
import net.messagehandler.listeners.entities.MobListener;
import net.messagehandler.listeners.chat.ignore.IgnoreChatEvent;
import net.messagehandler.listeners.chat.spy.ChatSpyEvent;
import net.messagehandler.listeners.chat.spy.CommandSpyEvent;
import net.messagehandler.listeners.inventory.words.BannedWordsListener;
import net.messagehandler.listeners.sign.SignColorListener;
import net.messagehandler.listeners.sign.SignFilterListener;
import net.messagehandler.messaging.MessageFile;
import net.messagehandler.messaging.MessageFormatter;
import net.messagehandler.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageHandler extends JavaPlugin {

    private static MessageHandler INSTANCE = null;
    private MessageFormatter messageFormatter = null;

    @Override
    public void onEnable() {
        messageFormatter = new MessageFormatter();
        INSTANCE = this;
        getServer().getConsoleSender().sendMessage(Utility.colorize(Utility.getPrefix() + "&bhas been enabled"));
        registerListeners();
        addCommands();
        initializeConfigs();
        Hook.loadDependencies();
        if(!setupVault())
            return;
        FileUtil autoBroadcastFile = new FileUtil(this, "settings/autobroadcast.yml", FileUtilType.DEFAULT);
        if(autoBroadcastFile.get().getBoolean("auto-broadcast-message.enable")) {
            Utility.autoBroadcastMessage(this);
        }
        if(autoBroadcastFile.get().getBoolean("auto-broadcast-title.enable")) {
            Utility.autoBroadcastTitle(this);
        }
        if(autoBroadcastFile.get().getBoolean("auto-broadcast-actionbar.enable")) {
            Utility.autoBroadcastActionBar();
        }
        if(autoBroadcastFile.get().getBoolean("auto-broadcast-message.per-world.enable")) {
            Utility.autoBroadcastPerWorld();
        }
        if(autoBroadcastFile.get().getBoolean("auto-broadcast-holo.enable")) {
            Utility.autoBroadcastHologram();
        }
        if(autoBroadcastFile.get().getBoolean("auto-broadcast-json.enable")) {
            Utility.autoBroadcastJSON();
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            User user = new User(player);
            if(!user.hasData()) {
                FileConfiguration rules = Utility.getConfigByFile("settings/rules.yml", FileUtilType.DEFAULT);
                if(!rules.getBoolean("prompt")) {
                    user.setData(DataType.DATE_JOINED, Utility.getMonthToday() + " " + Utility.getDateToday());
                    user.setData(DataType.NAME, user.getName());
                    return;
                }
                DataManager.rulesPrompt.add(user.getUuid());
                for(String rule : rules.getStringList("Rules")) {
                    user.sendMessage(rule);
                    user.sendMessage("&bType &eI agree &bto agree");
                }
            }
        }
        Utility.reloadTab(this);
        Utility.reloadNameTag(this);

    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(Utility.colorize(Utility.getPrefix() + "&bhas been disabled"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setDisplayName(player.getName());
            player.setPlayerListHeaderFooter(null, null);
        }
        Utility.removeAllNameTag();
    }

    public static MessageHandler getInstance() {
        return INSTANCE;
    }

    public MessageFormatter getMessageFormatter() {
        return messageFormatter;
    }

    void registerListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerPingListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
        getServer().getPluginManager().registerEvents(new CommandEvent(this), this);
        getServer().getPluginManager().registerEvents(new AdvertiseEvent(this), this);
        getServer().getPluginManager().registerEvents(new CapsEvent(this), this);
        getServer().getPluginManager().registerEvents(new DingPlayerEvent(this), this);
        getServer().getPluginManager().registerEvents(new GrammarEvent(this), this);
        getServer().getPluginManager().registerEvents(new SpamEvent(this), this);
        getServer().getPluginManager().registerEvents(new ChatRadiusEvent(this), this);
        getServer().getPluginManager().registerEvents(new StaffChatEvent(this), this);
        getServer().getPluginManager().registerEvents(new ChatFormatEvent(this), this);
        getServer().getPluginManager().registerEvents(new IgnoreChatEvent(), this);
        getServer().getPluginManager().registerEvents(new CustomAliasesEvent(), this);
        getServer().getPluginManager().registerEvents(new LogListener(), this);
        getServer().getPluginManager().registerEvents(new AntiUnicodeListener(), this);
        getServer().getPluginManager().registerEvents(new ExtraListener(), this);
        getServer().getPluginManager().registerEvents(new JoinSendMessageListener(), this);
        getServer().getPluginManager().registerEvents(new BroadcastHoloListener(), this);
        registerInventoryListeners();
        registerAFKListeners();
        registerSpyListeners();
    }

    void registerSpyListeners() {
        getServer().getPluginManager().registerEvents(new ChatSpyEvent(),this);
        getServer().getPluginManager().registerEvents(new CommandSpyEvent(),this);
    }

    void registerAFKListeners() {
        getServer().getPluginManager().registerEvents(new GodModeEvent(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveEvent(this), this);
        getServer().getPluginManager().registerEvents(new VanishAFKEvent(this), this);
    }

    void registerInventoryListeners() {
        getServer().getPluginManager().registerEvents(new MenuClick(this), this);
        getServer().getPluginManager().registerEvents(new ChatChannel(), this);
        registerGroupChatListeners();
        registerTicketListener();
        registerEmailListener();
        registerOnlineListener();
        registerSignListener();
        registerBookListener();
        registerAnvilListener();
    }

    void registerGroupChatListeners() {
        getServer().getPluginManager().registerEvents(new GroupChatClick(), this);
        getServer().getPluginManager().registerEvents(new GroupsClick(), this);
        getServer().getPluginManager().registerEvents(new GroupClickListener(), this);
        getServer().getPluginManager().registerEvents(new MemberClick(), this);
    }

    void registerTicketListener() {
        getServer().getPluginManager().registerEvents(new TicketMenuClick(), this);
        getServer().getPluginManager().registerEvents(new TicketCategoryClick(), this);
        getServer().getPluginManager().registerEvents(new TicketCreateListener(), this);
        getServer().getPluginManager().registerEvents(new TicketsClick(), this);
        getServer().getPluginManager().registerEvents(new TicketReplyListener(), this);
        getServer().getPluginManager().registerEvents(new MobListener(), this);
    }

    void registerEmailListener() {
        getServer().getPluginManager().registerEvents(new EmailAttachmentListener(), this);
        getServer().getPluginManager().registerEvents(new EmailLogoListener(), this);
        getServer().getPluginManager().registerEvents(new MailListener(), this);
        getServer().getPluginManager().registerEvents(new EmailSendListener(), this);
        getServer().getPluginManager().registerEvents(new EmailViewer(), this);
        getServer().getPluginManager().registerEvents(new EmailInboxListener(), this);
    }

    void registerOnlineListener() {
        getServer().getPluginManager().registerEvents(new OnlineListener(), this);
        getServer().getPluginManager().registerEvents(new StaffsListener(), this);
        getServer().getPluginManager().registerEvents(new PreferenceListener(), this);
        getServer().getPluginManager().registerEvents(new CustomizationListener(), this);
        getServer().getPluginManager().registerEvents(new NameTagListener(), this);
        getServer().getPluginManager().registerEvents(new ChatPrefixListener(), this);
        getServer().getPluginManager().registerEvents(new NameTagColorsListener(), this);
        registerBannedWords();
    }

    void registerSignListener() {
        getServer().getPluginManager().registerEvents(new SignColorListener(), this);
        getServer().getPluginManager().registerEvents(new SignFilterListener(), this);
    }

    void registerBookListener() {
        getServer().getPluginManager().registerEvents(new BookFilterListener(), this);
        getServer().getPluginManager().registerEvents(new BookColorListener(), this);
    }

    void registerAnvilListener() {
        getServer().getPluginManager().registerEvents(new AnvilColorListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilFilterListener(), this);
    }

    void registerBannedWords() {
        getServer().getPluginManager().registerEvents(new BannedWordsListener(), this);
    }

    void initializeConfigs() {
        saveDefaultConfig();
        MessageFile messageFile = new MessageFile("settings/messages.yml");
        messageFile.initialize();
        Utility.setupFile("settings/words.yml", FileUtilType.DEFAULT);
        Utility.setupFile("groupchats.yml", FileUtilType.DATA);
        Utility.setupFile("settings/help.yml", FileUtilType.DEFAULT);
        Utility.setupFile("settings/rules.yml", FileUtilType.DEFAULT);
        Utility.setupFile("settings/motd.yml", FileUtilType.DEFAULT);
        Utility.setupFile("settings/tab.yml", FileUtilType.DEFAULT);
        Utility.setupFile("settings/autobroadcast.yml", FileUtilType.DEFAULT);
        Utility.setupFile("playerdata.yml", FileUtilType.DATA);
        Utility.setupFile("tickets.yml", FileUtilType.DATA);
        Utility.setupFile("settings/ticket.yml", FileUtilType.DEFAULT);
        Utility.setupFile("settings/aliases.yml", FileUtilType.DEFAULT);
        Utility.setupFile("settings/staff.yml", FileUtilType.DEFAULT);
    }

    void addCommands() {
        this.getCommand("messagehandler").setExecutor(new CommandHandler());
        this.getCommand("afk").setExecutor(new CommandAFK(this));
        this.getCommand("rules").setExecutor(new CommandRules(this));
        this.getCommand("vanish").setExecutor(new CommandVanish(this));
        this.getCommand("motd").setExecutor(new CommandMOTD(this));
        this.getCommand("broadcast").setExecutor(new CommandBroadcast(this));
        this.getCommand("clearchat").setExecutor(new CommandClearChat(this));
        this.getCommand("nickname").setExecutor(new CommandNick(this));
        this.getCommand("realname").setExecutor(new CommandRealName(this));
        this.getCommand("commandspy").setExecutor(new CommandCommandSpy(this));
        this.getCommand("chatspy").setExecutor(new CommandChatSpy(this));
        this.getCommand("whisper").setExecutor(new CommandWhisper(this));
        this.getCommand("reply").setExecutor(new CommandReply(this));
        this.getCommand("ignore").setExecutor(new CommandIgnore(this));
        this.getCommand("ignoreall").setExecutor(new CommandIgnoreAll(this));
        this.getCommand("sendtitle").setExecutor(new CommandSendTitle(this));
        this.getCommand("warn").setExecutor(new CommandWarn(this));
        this.getCommand("help").setExecutor(new CommandHelp(this));
        this.getCommand("online").setExecutor(new CommandOnlineInventory());
        this.getCommand("staffs").setExecutor(new CommandStaffsInventory());
        this.getCommand("bannedwords").setExecutor(new CommandBannedWordsInventory());
        this.getCommand("chat").setExecutor(new CommandChatInventory());
        this.getCommand("groupchat").setExecutor(new CommandGroupChatInventory());
        this.getCommand("email").setExecutor(new CommandEmailInventory());
        this.getCommand("ticket").setExecutor(new CommandTicketInventory());
        this.getCommand("customization").setExecutor(new CommandCustomizationInventory());
    }


    public boolean setupVault() {
        if (Hook.isVaultLoaded()) {
            VaultHook.hook();
            return true;
        }
        getLogger().info("Vault is required to use message handler, disabling plugin!");
        Bukkit.getPluginManager().disablePlugin(this);
        return false;
    }
}
