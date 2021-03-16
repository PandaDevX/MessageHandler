package net.messagehandler.utility;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.messagehandler.MessageHandler;
import net.messagehandler.utility.events.BroadcastHoloEvent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import net.md_5.bungee.api.ChatColor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utility {

    protected static Pattern COLOR_PATTERN = Pattern.compile("(?i)&([0-9A-F])");
    protected static Pattern FORMAT_PATTERN = Pattern.compile("(?i)&([K-O])");
    protected static Pattern RESET_PATTERN = Pattern.compile("(?i)&([R])");
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    private static String theIP;
    private static int taskID = -1;
    private static BossBar bar;
    private static BufferedReader in;
    private static int timer = -1;
    public static String colorize(String message) {
        if (Version.getCurrentVersion().isNewer(Version.v1_15_R2)) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find())
                matcher.appendReplacement(buffer, ChatColor.of(matcher.group()).toString());
            return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String stripColor(String message) {
        return ChatColor.stripColor(message);
    }

    public static String getPrefix() {
        return MessageHandler.getInstance().getMessageFormatter().getMessageFile().getConfig().getString("prefix");
    }

    public static boolean isInt(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean searchPlugin(String plugin) {
        boolean hooked = (Bukkit.getPluginManager().getPlugin(plugin) != null);
        if (hooked)
            Bukkit.getLogger().info("[MessageHandler] Hooked into: " + plugin + " v" + Bukkit.getPluginManager().getPlugin(plugin).getDescription().getVersion());
        return hooked;
    }


    public static String formatString(String string) {
        string = FORMAT_PATTERN.matcher(string).replaceAll("§$1");
        string = RESET_PATTERN.matcher(string).replaceAll("§$1");
        return string;
    }

    public static String formatStringAll(String string) {
        string = COLOR_PATTERN.matcher(string).replaceAll("§$1");
        string = FORMAT_PATTERN.matcher(string).replaceAll("§$1");
        string = RESET_PATTERN.matcher(string).replaceAll("§$1");
        string = string.replaceAll("%", "\\%");
        return string;
    }

    public static Entity getEntityByUniqueId(String uniqueId) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getUniqueId().equals(UUID.fromString(uniqueId)))
                    return entity;
            }
        }

        return null;
    }

    public static void loadCustomNames() {
        if(!MessageHandler.getInstance().getConfig().getBoolean("Indicator.Custom Name")) return;
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(!(entity instanceof Mob)) return;
                if(entity.getCustomName() != null) return;
                Mob mob = (Mob)entity;
                int health = (int)mob.getHealth();
                int maxHealth = (int)mob.getMaxHealth();
                entity.setCustomName(Utility.colorize("&c" + entity.getName() + " &a" + health + " / " + maxHealth));
            }
        }
    }

    public static String formatColor(String string) {
        string = COLOR_PATTERN.matcher(string).replaceAll("§$1");
        string = RESET_PATTERN.matcher(string).replaceAll("§$1");
        string = string.replaceAll("%", "\\%");
        return string;
    }

    public static List<String> generateStringList(List<String> list, int page) {
        int maxPage = (int)Math.ceil(list.size() / 5.0D);
        if(page > maxPage)
            page = maxPage;
        int wordPage = (page-1) * 5;

        return list.subList(wordPage, wordPage + Math.min(list.size() - wordPage, 5));
    }

    public static <T> List<T> generateObjectList(List<T> list, int page, int perPage) {
        int maxPage = (int) Math.ceil(list.size() / (double)perPage);
        if(page > maxPage)
            page = maxPage;
        int wordPage = (page-1) * perPage;
        return list.subList(wordPage, wordPage + Math.min(list.size() - wordPage, perPage));
    }

    public static List<ItemStack> generateItemList(List<ItemStack> list, int page) {
        int maxPage = (int)Math.ceil(list.size()/36.0D);
        if(page > maxPage)
            page = maxPage;
        int itemPage = (page-1) * 36;

        return list.subList(itemPage, itemPage + Math.min(list.size() - itemPage, 36));
    }

    public static List<Material> generateMaterialList(List<Material> list, int page) {
        int maxPage = (int)Math.ceil(list.size()/36.0D);
        if(page > maxPage)
            page = maxPage;
        int itemPage = (page-1) * 36;

        return list.subList(itemPage, itemPage + Math.min(list.size() - itemPage, 36));
    }

    public static List<String> generateStringList(List<String> list, int page, int perPage) {
        double testPage = perPage;
        int maxPage = (int)Math.ceil(list.size() / testPage);
        if(page > maxPage)
            page = maxPage;
        int wordPage = (page-1) * perPage;

        return list.subList(wordPage, wordPage + Math.min(list.size() - wordPage, perPage));
    }

    public static ItemStack getPlayerHead(String player, String name, List<String> lore) {

        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");

        Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
        ItemStack item = new ItemStack(type, 1);
        if(!isNewVersion)
            item.setDurability((short)3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player);
        meta.setDisplayName(name);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getPlayerHead(String player, String name) {

        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");

        Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
        ItemStack item = new ItemStack(type, 1);
        if(!isNewVersion)
            item.setDurability((short)3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player);
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static String getMonthToday() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int num = localDate.getMonthValue() - 1;
        String month;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        month = months[num];
        return month;
    }

    public static String getDateToday() {
        SimpleDateFormat format = new SimpleDateFormat("dd yyyy");
        return format.format(new Date());
    }
    public static String getDateTodayLog() {
        SimpleDateFormat format = new SimpleDateFormat("dd-yyyy");
        return format.format(new Date());
    }

    public static boolean inRange(Player player, Player player2, int radius) {
        if(player2.getLocation().getWorld().equals(player.getLocation().getWorld())
        && player2.getLocation().distanceSquared(player.getLocation()) <= (radius*radius)) {
            return true;
        }
        return false;
    }

    public static boolean withinRange(Location locationFrom, Location locationTo, int radius) {
        if(locationTo.getWorld().equals(locationFrom.getWorld())
        && locationTo.distanceSquared(locationFrom) <= (radius*radius)) {
            return true;
        }
        return false;
    }

    public static int parseInterval(String toParse) {
        int result = 0;
        if(toParse.contains(" ")) {
            String[] raw = toParse.split(" ");
            for(int i = 0; i < raw.length; i++) {
                String parse = raw[i];
                String toMultiply = parse.substring(1);
                String multiplicand = String.valueOf(parse.charAt(0));
                if(!isInt(multiplicand)) {
                    return 1;
                }
                switch (toMultiply.toLowerCase()) {
                    case "w":
                        if(isInt(multiplicand)) {
                            int x = Integer.parseInt(multiplicand);
                            result += (x*604800);
                        }
                        break;
                    case "d":
                        if(isInt(multiplicand)) {
                            int x = Integer.parseInt(multiplicand);
                            result += (x*86400);
                        }
                        break;
                    case "h":
                        if(isInt(multiplicand)) {
                            int x = Integer.parseInt(multiplicand);
                            result += (x*3600);
                        }
                        break;
                    case "m":
                        if(isInt(multiplicand)) {
                            int x = Integer.parseInt(multiplicand);
                            result += (x*60);
                        }
                        break;
                    default:
                        if(isInt(multiplicand)) {
                            int x = Integer.parseInt(multiplicand);
                            result += x;
                        }
                        break;
                }
                return result;
            }
        }
        String toMultiply = toParse.substring(1);
        String multiplicand = String.valueOf(toParse.charAt(0));
        if(!isInt(multiplicand)) {
            return 1;
        }
        switch (toMultiply.toLowerCase()) {
            case "w":
                if(isInt(multiplicand)) {
                    int x = Integer.parseInt(multiplicand);
                    result += (x*604800);
                }
                break;
            case "d":
                if(isInt(multiplicand)) {
                    int x = Integer.parseInt(multiplicand);
                    result += (x*86400);
                }
                break;
            case "h":
                if(isInt(multiplicand)) {
                    int x = Integer.parseInt(multiplicand);
                    result += (x*3600);
                }
                break;
            case "m":
                if(isInt(multiplicand)) {
                    int x = Integer.parseInt(multiplicand);
                    result += (x*60);
                }
                break;
            default:
                if(isInt(multiplicand)) {
                    int x = Integer.parseInt(multiplicand);
                    result += x;
                }
                break;
        }
        return result;
    }

    public static void autoBroadcastMessage(MessageHandler plugin) {
        FileUtil autoBroadcastFile = new FileUtil(plugin, "settings/autobroadcast.yml", FileUtilType.DEFAULT);
        FileConfiguration config = autoBroadcastFile.get();
        List<String> MESSAGES = config.getStringList("auto-broadcast-message.messages");
        int INTERVAL = parseInterval(config.getString("auto-broadcast-message.interval"));
        new BukkitRunnable() {
            TextComponent builder = new TextComponent();
            int line = 0;
            public void run() {
                if (this.line >= MESSAGES.size())
                    this.line = 0;
                String msg = MESSAGES.get(line);
                String[] msgs = msg.split(" ");
                if(msg.contains("(") || msg.contains(")")) {
                    TextComponent theText = new TextComponent("");
                    for(int i = 0; i < msgs.length; i++) {
                        if(!msgs[i].contains("(") && !msgs[i].contains(")")) {
                            theText.addExtra(new TextComponent(TextComponent.fromLegacyText(Utility.colorize(msgs[i]) + " ")));
                            continue;
                        }
                        String key = msgs[i].substring(1, (msgs[i].length() - 1));
                        String keyName = Utility.colorize(config.getString("Text Component." + key + ".message"));
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(keyName));
                        if(config.get("Text Component." + key + ".click")!= null) {
                            if (config.get("Text Component." + key + ".click.open_url") != null) {
                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("Text Component." + key + ".click.open_url")));
                            }
                            if (config.get("Text Component." + key + ".click.copy_clipboard") != null) {
                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, config.getString("Text Component." + key + ".click.copy_clipboard")));
                            }
                            if (config.get("Text Component." + key + ".click.run_command") != null) {
                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, config.getString("Text Component." + key + ".click.run_command")));
                            }
                            if (config.get("Text Component." + key + ".click.suggest_command") != null) {
                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, config.getString("Text Component." + key + ".click.suggest_command")));
                            }
                        }
                        if (config.get("Text Component." + key + ".hover") != null) {
                            ComponentBuilder builder = new ComponentBuilder();
                            builder.append(TextComponent.fromLegacyText(Utility.colorize(config.getString("Text Component." + key + ".hover"))));
                            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, builder.create()));
                        }
                        theText.addExtra(textComponent);
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            player.spigot().sendMessage(ChatMessageType.CHAT, theText);
                        }
                    }
                }else {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(TextComponent.fromLegacyText(Utility.colorize(msg))));
                    }
                }
                    this.line++;
            }
        }.runTaskTimer(plugin, 0L, 20L * Long.valueOf(INTERVAL));
    }

    public static void autoBroadcastTitle(MessageHandler plugin) {
        FileUtil autoBroadcastFile = new FileUtil(plugin, "settings/autobroadcast.yml", FileUtilType.DEFAULT);
        FileConfiguration config = autoBroadcastFile.get();
        int INTERVAL = parseInterval(config.getString("auto-broadcast-title.interval"));
        List<String> TITLES = new ArrayList<>(config.getConfigurationSection("auto-broadcast-title.titles").getKeys(false));
        new BukkitRunnable() {
            int line = 0;
            public void run() {
                if(this.line >= TITLES.size())
                    this.line = 0;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    String title = config.getString("auto-broadcast-title.titles." + TITLES.get(line) + ".title");
                    String subTitle = config.getString("auto-broadcast-title.titles." + TITLES.get(line) + ".sub-title");
                    int fadeIn = config.getInt("auto-broadcast-title.titles." + TITLES.get(line) + ".fade-in");
                    int stay = config.getInt("auto-broadcast-title.titles." + TITLES.get(line) + ".stay");
                    int fadeOut = config.getInt("auto-broadcast-title.titles" + TITLES.get(line) + ".fade-out");

                    player.sendTitle(Utility.colorize(title), Utility.colorize(subTitle), fadeIn, stay, fadeOut);
                }
                this.line++;
            }
        }.runTaskTimer(plugin, 0L, 20L * Long.valueOf(INTERVAL));
    }

    public static void autoBroadcastActionBar() {
        FileConfiguration config = Utility.getConfigByFile("settings/autobroadcast.yml", FileUtilType.DEFAULT);
        int interval = parseInterval(config.getString("auto-broadcast-actionbar.interval"));
        List<String> messages = config.getStringList("auto-broadcast-actionbar.messages");
        new BukkitRunnable() {
            int line = 0;
            public void run() {
                if(this.line >= messages.size())
                    this.line = 0;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Utility.parseMessage(MessageHandler.getInstance(), messages.get(line))));
                }
            }
        }.runTaskTimer(MessageHandler.getInstance(), 0L, 20L * Long.valueOf(interval));
    }

    public static void autoBroadcastHologram() {
        FileConfiguration config = Utility.getConfigByFile("settings/autobroadcast.yml", FileUtilType.DEFAULT);
        int last = config.getInt("auto-broadcast-holo.last");
        int interval = parseInterval(config.getString("auto-broadcast-holo.interval"));
        List<String> set = new ArrayList<>(config.getConfigurationSection("auto-broadcast-holo.messages").getKeys(false));
        new BukkitRunnable() {
            int line = 0;
            public void run() {
                if(this.line >= set.size()) {
                    this.line = 0;
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    List<String> messages = config.getStringList("auto-broadcast-holo.messages." + set.get(line));
                    BroadcastHoloEvent broadcastHoloEvent = new BroadcastHoloEvent(player);
                    broadcastHoloEvent.setMessage(messages);
                    broadcastHoloEvent.setDeathInterval(last);
                    Bukkit.getPluginManager().callEvent(broadcastHoloEvent);
                }
            }
        }.runTaskTimer(MessageHandler.getInstance(), 0L, 20L * Long.valueOf(interval));
    }

    public static void autoBroadcastJSON() {
        FileConfiguration config = Utility.getConfigByFile("settings/autobroadcast.yml", FileUtilType.DEFAULT);
        int interval = parseInterval(config.getString("auto-broadcast-json.interval"));
        List<String> messages = config.getStringList("auto-broadcast-json.messages");
        new BukkitRunnable() {
            int line = 0;
            public void run() {
                if(line >= messages.size()) {
                    line = 0;
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ComponentSerializer.parse(messages.get(line)));
                }
                }
        }.runTaskTimer(MessageHandler.getInstance(), 0L, 20L * Long.valueOf(interval));
    }

    public static int getPlayTimeInSeconds(Player player) {
        int seconds = player.getStatistic(Statistic.PLAY_ONE_MINUTE)/20;
        return seconds;
    }

    public static void autoBroadcastPerWorld() {
        FileConfiguration config = Utility.getConfigByFile("settings/autobroadcast.yml", FileUtilType.DEFAULT);
        int interval = parseInterval(config.getString("auto-broadcast-message.per-world.interval"));
        Set<String> set = config.getConfigurationSection("auto-broadcast-message.per-world").getKeys(false);
        if(set.size() > 1) {
            new BukkitRunnable() {
                public void run() {
                    for(String world : set) {
                        int line = 0;
                        if(world.equals("interval")) continue;
                        if(world.equals("enable")) continue;
                        List<String> messages = config.getStringList("auto-broadcast-message.per-world." + world);
                        if(line >= messages.size())
                            line = 0;
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            if(player.getWorld().equals(world)) {
                                User user = new User(player);
                                user.sendMessage(messages.get(line));
                            }
                        }
                    }
                }
            }.runTaskTimer(MessageHandler.getInstance(), 0L, 20L * Long.valueOf(interval));
        }
    }

    private static int percentageOfMatch(String message, String toMatch) {
        char[] messageCharacters = message.toCharArray();
        char[] toMatchCharacters = toMatch.toCharArray();
        int totalMessage = messageCharacters.length;
        int positiveCounter = 0;
        int negativeCounter;
        int totalToMatch = toMatchCharacters.length;
        if(totalMessage >= totalToMatch) {
            for(int i = 0; i < totalToMatch; i++) {
                if(toMatchCharacters[i] == messageCharacters[i]) {
                    positiveCounter+=1;
                }
            }
            negativeCounter = totalMessage - positiveCounter;
            if(negativeCounter >= positiveCounter) {
                return 0;
            }
            return (int) (1.0D * positiveCounter / (positiveCounter + negativeCounter) * 100.0D);
        }
        for(int i = 0; i < totalMessage; i++) {
            if(toMatchCharacters[i] == messageCharacters[i]) {
                positiveCounter+=1;
            }
        }
        negativeCounter = totalMessage - positiveCounter;
        return (int) (1.0D * positiveCounter / (positiveCounter + negativeCounter) * 100.0D);
    }

    public static boolean isMessageSimilarToBefore(String before, String now, int similarity) {
        return percentageOfMatch(before, now) >= similarity;
    }

    public static String getPing(Player player) {
        DecimalFormat format = new DecimalFormat("#,###");
        if(getPingNumber(player) <= 80) {
            return colorize("&a" + format.format(getPingNumber(player)));
        }else if(getPingNumber(player) <= 130 && getPingNumber(player) > 80) {
            return colorize("&c" + format.format(getPingNumber(player)));
        }
        return colorize("&4" + format.format(getPingNumber(player)));
    }

    private static int getPingNumber(Player player) {
        String pName = Bukkit.getServer().getClass().getPackage().getName();
        String version = pName.substring(pName.lastIndexOf(".") + 1, pName.length());
        try {
            Class<?> CPClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Object CraftPlayer = CPClass.cast(player);
            Method getHandle = CraftPlayer.getClass().getMethod("getHandle", new Class[0]);
            Object EntityPlayer = getHandle.invoke(CraftPlayer, new Object[0]);
            Field ping = EntityPlayer.getClass().getDeclaredField("ping");
            return ping.getInt(EntityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void removeAllNameTag() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setPlayerListName(null);
        }
    }

    public static boolean sensor(List<String> words, List<String> bannedWords, User user) {
        boolean found = false;
        StringBuilder builder = new StringBuilder();
        for(int x = 0; x < words.size(); x++) {
            for(int y = 0; y < bannedWords.size(); y++) {
                String findX = words.get(x).toLowerCase();
                findX = stripColor(findX);
                String findY = bannedWords.get(y).toLowerCase();
                findY = stripColor(findY);
                if(findX.contains(findY)) {
                    found = true;
                }
            }
            if(words.size() > 1) {
                builder.append(words.get(x));
            }
            for(int i = 0; i < bannedWords.size(); i++) {
                if(builder.toString().toLowerCase().contains(bannedWords.get(i).toLowerCase())) {
                    found = true;
                }
            }
            // Blocks FuCk
            // Blocks f u c k
            // Blocks fuuu cckkkk
            // Blocks fu123/ck
            // Blocks fcuk
            // Blocks fc123/@uk
            // Blocks fc123123ccuk
            // Blocks fcccu 123@ck
            if(!bannedWords.isEmpty()) {
                StringBuilder sentence = new StringBuilder();
                Iterator<String> bannedWordsIterator = bannedWords.iterator();
                while(bannedWordsIterator.hasNext()) {
                    Iterator<String> wordsIterator = words.iterator();
                    String banWord = bannedWordsIterator.next();
                    while(wordsIterator.hasNext()) {
                        String word = wordsIterator.next();
                        word = filterDuplicate(word);
                        word = filterNonCharacter(word);
                        if(word.equalsIgnoreCase(banWord)) {
                            found = true;
                        }
                        if(isAnagramSort(word.toLowerCase(), banWord.toLowerCase())) {
                            found = true;
                        }
                        sentence.append(word);
                    }
                    banWord = filterDuplicate(banWord);
                    banWord = filterNonCharacter(banWord);
                    if(sentence.toString().toLowerCase().contains(banWord)) {
                        found = true;
                    }
                    if(isAnagramSort(sentence.toString().toLowerCase(), banWord.toLowerCase())) {
                        found = true;
                    }
                }
            }
        }
        if(found) {
            FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), Utility.getDateTodayLog() + ".txt", FileUtilType.SWEARLOG);
            fileUtil.setup();
            FileConfiguration config = MessageHandler.getInstance().getConfig();

            if(!config.getBoolean("Logs.Swear")) {
                return found;
            }
            Date time = Calendar.getInstance().getTime();

            try {
                FileWriter write = new FileWriter(fileUtil.getFile(), true);
                BufferedWriter writer = new BufferedWriter(write);
                writer.write("<<" + time + ">>" + user.getName() + " => " + org.bukkit.ChatColor.stripColor(String.join(" ", words)));
                writer.newLine();
                write.flush();
                writer.close();
            }catch (IOException ex) {
                ex.printStackTrace();
            }
            user.warn(MessageHandler.getInstance().getConfig().getString("Anti Swear.Warn", "Swearing"), "Console");

            FileConfiguration wordConfig = Utility.getConfigByFile("settings/words.yml", FileUtilType.DEFAULT);
            if(wordConfig.getBoolean("economy.enable")) {
                if(VaultHook.withdrawMoney(user.getPlayer(), wordConfig.getDouble("economy.cost"))) {
                    DecimalFormat format = new DecimalFormat("#,###");
                    user.sendActionBarMessage("&cYou have been penalized: &f(&c-&f) &f" + wordConfig.getString("economy.sign") + format.format(wordConfig.getDouble("economy.cost")));
                }
            }
        }
        return found;
    }

    public static String filterDuplicate(String word) {
        char[] chars = word.toCharArray();
        Set<Character> charSet = new LinkedHashSet<Character>();
        for (char c : chars) {
            charSet.add(c);
        }

        StringBuilder sb = new StringBuilder();
        for (Character character : charSet) {
            sb.append(character);
        }
        return sb.toString();
    }

    public static boolean isAnagramSort(String word1, String word2) {
        if(word1.length() == word2.length()) {
            char[] a1 = word1.toCharArray();
            char[] a2 = word2.toCharArray();
            Arrays.sort(a1);
            Arrays.sort(a2);
            return Arrays.equals(a1, a2);
        }
        return false;
    }

    public static String filterNonCharacter(String word) {
        return word.replaceAll("[^a-zA-Z]", "");
    }

    public static boolean hasNonCharacter(String word) {
        return ((!word.equals("")) && (word != null) && (!word.matches("^[a-zA-Z]*$")));
    }


    public static void removeNameTag(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Team currentTeam = board.getPlayerTeam(player);
        if(currentTeam != null) {
            currentTeam.removePlayer(player);
        }
    }

    public static void reloadNameTag(MessageHandler plugin) {
        if(plugin.getConfig().getBoolean("Custom NameTag.Enable")) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getMainScoreboard();
            if(plugin.getConfig().getBoolean("Custom.NameTag.Show Player Health")) {
                if(board.getObjective("health") != null)
                    board.getObjective("health").unregister();
                Objective objective = board.registerNewObjective("health", "health");
                objective.setDisplayName(colorize("&c&l❤"));
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            }

            for(Player player : Bukkit.getOnlinePlayers()) {
                Team currentTeam = board.getPlayerTeam(player);
                if(currentTeam != null)
                    currentTeam.removePlayer(player);
                User user = new User(player);
                if(user.hasCustomNameTag() && !user.isAFK()){
                    Team custom = board.getTeam(user.getName()) != null ? board.getTeam(user.getName()) : board.registerNewTeam(user.getName());
                    String p = !user.getNameTag()[0].equals("")? user.getNameTag()[0] : "";
                    custom.setPrefix(p + " ");
                    org.bukkit.ChatColor c = !user.getNameTag()[1].equals("") ? org.bukkit.ChatColor.valueOf(user.getNameTag()[1]) : org.bukkit.ChatColor.WHITE;
                    custom.setColor(c);
                    String s = !user.getNameTag()[1].equals("") ? user.getNameTag()[1] : "";
                    custom.setSuffix(" " + s);
                    custom.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    custom.addPlayer(user.getPlayer());
                    continue;
                }
                if(user.isInChannel() && !user.isAFK()) {
                    Team team = board.getTeam(user.getChannel()) != null ? board.getTeam(user.getChannel()) : board.registerNewTeam(user.getChannel());
                    team.setPrefix(Utility.colorize("&b&l" + user.getChannel()) + " ");
                    team.setColor(org.bukkit.ChatColor.RED);
                    team.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    team.addPlayer(user.getPlayer());
                    continue;
                }
                if (player.isOp() && !new User(player).isAFK()) {
                    Team op = board.getTeam("op") != null ? board.getTeam("op") : board.registerNewTeam("op");
                    op.setPrefix(Utility.colorize(plugin.getConfig().getString("Custom NameTag.OP.Prefix")));
                    op.setColor(org.bukkit.ChatColor.valueOf(plugin.getConfig().getString("Custom NameTag.OP.Color")));
                    op.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    op.addPlayer(player);
                }else if (new User(player).isAFK()) {
                    Team afk = board.getTeam("afk") != null ? board.getTeam("afk") : board.registerNewTeam("afk");
                    afk.setPrefix(Utility.colorize(plugin.getConfig().getString("Custom NameTag.AFK.Prefix")));
                    afk.setColor(org.bukkit.ChatColor.valueOf(plugin.getConfig().getString("Custom NameTag.AFK.Color")));
                    afk.addPlayer(player);
                }else {
                    for (String t : plugin.getConfig().getConfigurationSection("Custom NameTag.Groups").getKeys(false)) {
                        Team team = board.getTeam(t) != null ? board.getTeam(t) : board.registerNewTeam(t);
                        if (player.hasPermission("messagehandler.nametag." + team.getName())) {
                            team.setPrefix(Utility.colorize(plugin.getConfig().getString("Custom NameTag.groups." + team.getName() + ".Prefix")));
                            team.setColor(org.bukkit.ChatColor.valueOf(plugin.getConfig().getString("Custom NameTag." +team.getName() +".Color")));
                            team.setNameTagVisibility(NameTagVisibility.ALWAYS);
                            team.addPlayer(player);
                        }else {
                            Team def = board.getTeam("default") != null ? board.getTeam("default") : board.registerNewTeam("default");
                            def.setPrefix(Utility.colorize(plugin.getConfig().getString("Custom NameTag.Default.Prefix")));
                            def.setColor(org.bukkit.ChatColor.valueOf(plugin.getConfig().getString("Custom NameTag.Default.Color")));
                            def.setNameTagVisibility(NameTagVisibility.ALWAYS);
                            def.addPlayer(player);
                        }
                    }
                }
                player.setScoreboard(board);
            }
        }
    }

    public static void reloadTab(MessageHandler plugin) {
        FileUtil tabFile = new FileUtil(plugin, "settings/tab.yml", FileUtilType.DEFAULT);
        if(!tabFile.get().getBoolean("animated-tab")) return;
        TabManager tabManager = new TabManager(plugin);
        FileConfiguration config = tabFile.get();
        for (String header : config.getStringList("tab.header")) {
            String msg = header;
            msg = parseMessage(plugin, msg);
            tabManager.addHeader(msg);
        }
        for (String footer : config.getStringList("tab.footer")) {
            String word = footer;
            word = parseMessage(plugin, word);
            tabManager.addFooter(word);
        }
        tabManager.showTab();
    }

    public static void sendMessage(MessageHandler plugin, Player player, String message) {
        player.sendMessage(parseMessage(plugin, player, message));
    }

    public static boolean sensorAd(String message, User user) {
        message = stripColor(message);
        String[] msg = message.replaceAll("(dot|DOT|Dot|dOt|doT|DOt|dOT|DoT|d0t|D0T|D0t|d0t|d0T|D0t|d0T|D0T)", ".").trim().split(" ");
        Pattern validHostname = Pattern.compile("^(?=(?:.*?[\\.\\,]){1})(?:[a-z][a-z0-9-]*[a-z0-9](?=[\\.,][a-z]|$)[\\.,:;|\\\\]?)+$");
        Pattern validIpAddress = Pattern.compile("^(?:(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?::\\d*)?$", 2);
        Pattern firstPattern = Pattern.compile("(?i)(((([a-zA-Z0-9-]+\\.)+(gs|ts|adv|no|uk|us|de|eu|com|net|noip|to|gs|me|info|biz|tv|au|co|pl|cz))+(\\:[0-9]{2,5})?))");
        Pattern secondPattern = Pattern.compile("(?i)(((([0-9]{1,3}\\.){3}[0-9]{1,3})(\\:[0-9]{2,5})?))");
        Matcher firstMatch = firstPattern.matcher(message.toLowerCase());
        Matcher secondMatch = secondPattern.matcher(message.toLowerCase());
        boolean found = false;
        StringBuilder end = new StringBuilder();
        for (int x = 0; x < msg.length; x++) {
            String tempIP = msg[x].trim().toLowerCase().replaceAll("[\\(\\)!@#\\$%\\^\\s\\&\\*;\"'\\?><~`,\\\\a-zA-Z]", "");
            String tempHost = msg[x].trim().toLowerCase().replaceAll("[\\d\\s\\(\\)!@#\\$%\\^\\s\\&\\*:;\"'\\?><~`,\\\\]", "");
            Matcher matchIP = validIpAddress.matcher(tempIP);
            while (matchIP.find()) {
                theIP = msg[x];
                found = true;
            }
            Matcher matchHost = validHostname.matcher(tempHost);
            while (matchHost.find()) {
                theIP = msg[x];
                found = true;
            }
            while(firstMatch.find()) {
                found = true;
            }
            while(secondMatch.find()) {
                found = true;
            }
            tempHost = msg[x].toLowerCase();
            String[] d = "www. http .com .net .org .ru .uk .us .fr .co .ca .au".split(" ");
            for (String s : d) {
                if (tempHost.contains(s)) {
                    found=true;
                }
            }
            end.append(msg[x] + " ");
        }
        if(found) {
            FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), Utility.getDateTodayLog() + ".txt", FileUtilType.ADLOG);
            fileUtil.setup();
            FileConfiguration config = MessageHandler.getInstance().getConfig();

            if(!config.getBoolean("Logs.Advertise")) {
                return found;
            }
            Date time = Calendar.getInstance().getTime();

            try {
                FileWriter write = new FileWriter(fileUtil.getFile(), true);
                BufferedWriter writer = new BufferedWriter(write);
                writer.write("<<" + time + ">>" + user.getName() + " => " + org.bukkit.ChatColor.stripColor(end.toString()));
                writer.newLine();
                write.flush();
                writer.close();
            }catch (IOException ex) {
                ex.printStackTrace();
            }
            user.warn(MessageHandler.getInstance().getConfig().getString("Anti Ad.Warn", "Advertising"), "Console");
        }
        return found;
    }

    public static List<String> getWords() {
        return getConfigByFile("settings/words.yml", FileUtilType.DEFAULT).getStringList("words");
    }

    public static String parseMessage(MessageHandler plugin, Player player, String placeholders) {
        DecimalFormat format = new DecimalFormat("#,###");
        placeholders = Utility.colorize(placeholders.replace("{player}", player.getName()));
        placeholders = Utility.colorize(placeholders.replace("{world}", player.getLocation().getWorld().getName()));
        placeholders = Utility.colorize(placeholders.replace("{online}", format.format(Bukkit.getOnlinePlayers().size())));
        placeholders = Utility.colorize(placeholders.replace("{max}", format.format(Bukkit.getServer().getMaxPlayers())));
        placeholders = Utility.colorize(placeholders.replace("{server}", plugin.getConfig().getString("Server Name")));
        placeholders = Utility.colorize(placeholders.replace("{health}", format.format(player.getHealth())));
        placeholders = Utility.colorize(placeholders.replace("{maxhealth}", format.format(player.getMaxHealth())));
        placeholders = Utility.colorize(placeholders.replace("{exp}", format.format(player.getTotalExperience())));
        placeholders = Utility.colorize(placeholders.replace("{level}", format.format(player.getLevel())));
        placeholders = placeholders.replace("\\n", "\n");
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat day = new SimpleDateFormat("EEEE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        placeholders = Utility.colorize(placeholders.replace("{day}", day.format(date)));
        placeholders = Utility.colorize(placeholders.replace("{time}", time.format(date)));
        placeholders = Utility.colorize(placeholders.replace("{month}", getMonthToday()));
        placeholders = Utility.colorize(placeholders.replace("{year}", year.format(date)));
        placeholders = Utility.colorize(placeholders.replace("{date}", dateFormat.format(date)));
        User user = new User(player);
        if(user.hasNickName()) {
            player.setDisplayName(user.getNickName());
        }
        placeholders = Utility.colorize(placeholders.replace("{warn}", format.format(user.getWarnPoints())));
        placeholders = Utility.colorize(placeholders.replace("{displayname}", player.getDisplayName()));
        placeholders = Utility.colorize(placeholders.replace("{prefix}", user.getPrefix()));
        placeholders = Utility.colorize(placeholders.replace("{suffix}", user.getSuffix()));
        String channel = user.isInChannel() ? user.getChannel() : "none";
        placeholders = Utility.colorize(placeholders.replace("{groupchat}", channel));
        placeholders = Utility.colorize(placeholders.replace("{nickname}", user.getNickName()));
        placeholders = Utility.colorize(placeholders.replace("{uuid}", user.getUuid().toString()));
        if(Hook.isVaultLoaded()){
            placeholders = Utility.colorize(placeholders.replace("{vault_prefix}", VaultHook.getPlayerPrefix(player)));
            placeholders = Utility.colorize(placeholders.replace("{vault_suffix}", VaultHook.getPlayerSuffix(player)));
            placeholders = Utility.colorize(placeholders.replace("{vault_group}", VaultHook.getPlayerGroup(player)));
            placeholders = Utility.colorize(placeholders.replace("{vault_balance}", format.format(VaultHook.getPlayerBalance(player))));
        }
        setupPlaceholderAPI(player, placeholders);
        return Utility.colorize(placeholders);
    }

    public static String setupPlaceholderAPI(Player player, String message) {
        String placeholders = message;
        if (Hook.isPlaceholderAPILoaded() && PlaceholderAPI.containsPlaceholders(placeholders))
            placeholders = PlaceholderAPI.setPlaceholders(player, placeholders);
        return placeholders;
    }

    public static String getMaxPlayers() {
        return new DecimalFormat("#,###").format(Bukkit.getServer().getMaxPlayers());
    }

    public static String getOnlinePlayers() {
        return new DecimalFormat("#,###").format(Bukkit.getServer().getOnlinePlayers().size());
    }

    public static String parseMessage(MessageHandler plugin, String placeholders) {
        DecimalFormat format = new DecimalFormat("#,###");
        placeholders = Utility.colorize(placeholders.replace("{online}", format.format(Bukkit.getOnlinePlayers().size())));
        placeholders = Utility.colorize(placeholders.replace("{max}", format.format(Bukkit.getServer().getMaxPlayers())));
        placeholders = Utility.colorize(placeholders.replace("{server}", MessageHandler.getInstance().getConfig().getString("Server Name")));
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat day = new SimpleDateFormat("EEEE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        placeholders = Utility.colorize(placeholders.replace("{day}", day.format(date)));
        placeholders = Utility.colorize(placeholders.replace("{time}", time.format(date)));
        placeholders = Utility.colorize(placeholders.replace("{month}", getMonthToday()));
        placeholders = Utility.colorize(placeholders.replace("{year}", year.format(date)));
        placeholders = Utility.colorize(placeholders.replace("{date}", dateFormat.format(date)));
        placeholders = placeholders.replace("\\n", "\n");
        return Utility.colorize(placeholders);
    }

    public static String getMessageDefault(MessageHandler plugin, String fileName, String path) {
        FileUtil fileUtil = new FileUtil(plugin, fileName, FileUtilType.DEFAULT);
        return fileUtil.get().getString(path);
    }

    public static String getMessageData(MessageHandler plugin, String fileName, String path) {
        FileUtil fileUtil = new FileUtil(plugin, fileName, FileUtilType.DATA);
        return fileUtil.get().getString(path);
    }

    public static void sendTitle(MessageHandler plugin, Player player, String message, int fadeIn, int stay, int fadeOut) {
        String msg = message;
        parseMessage(plugin, player, message);
        if(msg.contains(":")) {
            String[] raw = msg.split(":");
            player.sendTitle(Utility.colorize(raw[0]), Utility.colorize(raw[1]), fadeIn, stay, fadeOut);
            return;
        }
        player.sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize(msg), fadeIn, stay, fadeOut);
    }

    public static FileConfiguration getConfigByFile(String fileName, FileUtilType fileType) {
        return new FileUtil(MessageHandler.getInstance(), fileName, fileType).get();
    }

    public static void saveConfigByFileName(String fileName, FileUtilType fileType) {
        new FileUtil(MessageHandler.getInstance(), fileName, fileType).save();
    }

    public static void setupFile(String fileName, FileUtilType fileType) {
        new FileUtil(MessageHandler.getInstance(), fileName, fileType).setup();
    }

    public static ItemStack createGUIItem(String name, List<String> lore, Material materialType) {
        ItemStack itemStack = new ItemStack(materialType);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Utility.colorize(name));
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static List<String> colorizeList(List<String> list, Player player) {
        for(int i = 0; i < list.size(); i++) {
            list.set(i, Utility.parseMessage(MessageHandler.getInstance(), player,  list.get(i)));
        }
        return list;
    }

    public static ItemStack createGUIItem(String name, Material materialType) {
        ItemStack itemStack = new ItemStack(materialType);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Utility.colorize(name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static HashMap<String, String> accessInboxMap(User user) {
        HashMap<String, String> raw = new HashMap<>();
        FileConfiguration playerData = Utility.getConfigByFile("playerdata.yml", FileUtilType.DATA);
        if(playerData.get(user.getUuid().toString() + ".mail") == null) return raw;
        for(String email : playerData.getConfigurationSection(user.getUuid().toString() + ".mail").getKeys(false)) {
            raw.put(email + " sender", playerData.getString(user.getUuid().toString() + ".mail." + email + ".sender"));
            raw.put(email + " logo", playerData.getString(user.getUuid().toString() + ".mail." + email + ".logo"));
            raw.put(email + " subject", playerData.getString(user.getUuid().toString() + ".mail." + email + ".subject"));
            raw.put(email + " message", playerData.getString(user.getUuid().toString() + ".mail." + email + ".message"));
        }
        return raw;
    }

    public static String getItemName(ItemStack itemStack) {
        return itemStack.getItemMeta().getDisplayName();
    }

    public static String createRandomID() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++)
            sb.append(chars[rnd.nextInt(chars.length)]);
        sb.append("-" + (100000 + rnd.nextInt(900000)));
        return sb.toString();
    }

    public static String getUUID(String name) {
        String uuid = "";
        try {
            in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream()));
            uuid = (((JsonObject)new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "");
            uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            in.close();
        } catch (Exception e) {
            System.out.println("Unable to get UUID of: " + name + "!");
            uuid = "er";
        }
        return uuid;
    }
}
