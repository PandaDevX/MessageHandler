package net.messagehandler.utility;

import net.messagehandler.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {

    private File file;
    private FileConfiguration config;
    private final MessageHandler plugin;
    private final String name;
    private final FileUtilType type;

    public FileUtil(MessageHandler plugin, String name, FileUtilType type) {
        this.plugin = plugin;
        this.name = name;
        this.type = type;
    }

    public void setup() {
        if(type == FileUtilType.DATA) {
            File folder = new File(plugin.getDataFolder(), "data");
            if(!folder.exists())
                if(folder.mkdirs()) {
                    Bukkit.getServer().getConsoleSender().sendMessage("&6MESSAGEHANDLER &e>>>> &bdata folder created");
                }
            file = new File(plugin.getDataFolder() + "/data/" + name);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            config = YamlConfiguration.loadConfiguration(file);
        }else if(type == FileUtilType.DEFAULT) {
            File folder = new File(plugin.getDataFolder(), "settings");
            if(!folder.exists())
                folder.mkdirs();
            if(file == null) {
                file = new File(plugin.getDataFolder(), name);
            }
            if(!file.exists()) {
                plugin.saveResource(name, false);
            }
        }else if(type == FileUtilType.COMMANDLOG) {
            File folder = new File(plugin.getDataFolder(), "logs/commands/" + Utility.getMonthToday());
            if(!folder.exists())
                folder.mkdirs();
            file = new File(plugin.getDataFolder() + "/logs/commands/" + Utility.getMonthToday() + "/" + name);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(type == FileUtilType.CHATLOG) {
            File folder = new File(plugin.getDataFolder(), "logs/chats/" + Utility.getMonthToday());
            if(!folder.exists())
                folder.mkdirs();
            file = new File(plugin.getDataFolder() + "/logs/chats/" + Utility.getMonthToday() + "/" + name);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if(type == FileUtilType.SWEARLOG) {
            File folder = new File(plugin.getDataFolder(), "logs/swear/" + Utility.getMonthToday());
            if(!folder.exists())
                folder.mkdirs();
            file = new File(plugin.getDataFolder() + "/logs/swear/" + Utility.getMonthToday() + "/" + name);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(type == FileUtilType.ADLOG) {
            File folder = new File(plugin.getDataFolder(), "logs/advertise/" + Utility.getMonthToday());
            if(!folder.exists())
                folder.mkdirs();
            file = new File(plugin.getDataFolder() + "/logs/advertise/" + Utility.getMonthToday() + "/" + name);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(type == FileUtilType.WARNLOG) {
            File folder = new File(plugin.getDataFolder(), "logs/warn/" + Utility.getMonthToday());
            if(!folder.exists())
                folder.mkdirs();
            file = new File(plugin.getDataFolder() + "/logs/warn/" + Utility.getMonthToday() + "/" + name);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save() {
        try {
            config.save(file);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration get() {
        if(config == null)
            reload();
        return config;
    }

    public void reload() {
        if(this.type == FileUtilType.DEFAULT) {
            if(file == null)
                file = new File(plugin.getDataFolder(), name);
            config = YamlConfiguration.loadConfiguration(file);

            InputStream defaultStream = plugin.getResource(name);
            if(defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
                config.setDefaults(defaultConfig);
            }
        }
        if(this.type == FileUtilType.DATA) {
            File folder = new File(plugin.getDataFolder(), "data");
            if(!folder.exists())
                folder.mkdirs();
            if(file == null)
                file = new File(plugin.getDataFolder() + "/data/" + name);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public File getFile() {
        return file;
    }
}
