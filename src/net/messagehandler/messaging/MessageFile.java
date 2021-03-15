package net.messagehandler.messaging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import net.messagehandler.MessageHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageFile {

    private File file = null;
    private FileConfiguration config = null;
    private final String name;


    public MessageFile(String name) {
        this.name = name;
    }

    public FileConfiguration getConfig() {
        if(config == null) {
            reloadConfig();
        }
        return config;
    }

    public void reloadConfig() {
        if(file == null)
            file = new File(MessageHandler.getInstance().getDataFolder(), name);
        config = YamlConfiguration.loadConfiguration(file);

        InputStream defaultStream = MessageHandler.getInstance().getResource(name);
        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);
        }
    }

    public void saveConfig() {
        if(config == null || file == null)
            return;
        try {
            config.save(file);
        }catch(IOException e) {
            MessageHandler.getInstance().getLogger().log(Level.SEVERE, "Could not save config to " + file, e);
        }
    }

    public void saveDefaultConfig() {
        if(file == null) {
            file = new File(MessageHandler.getInstance().getDataFolder(), name);
        }
        if(!file.exists()) {
            MessageHandler.getInstance().saveResource(name, false);
        }
    }

    public String get(String key) {
        return getConfig().getString(key);
    }

    public void initialize() {
        saveDefaultConfig();
    }

}