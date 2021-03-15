package net.messagehandler.utility.picture;

import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.Utility;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PictureWrapper extends BukkitRunnable {
    private PictureUtil pictureUtil;

    private FileConfiguration motd = Utility.getConfigByFile("settings/motd.yml", FileUtilType.DEFAULT);

    private Player player;

    public PictureWrapper(Player player) {
        this.pictureUtil = new PictureUtil();
        this.player = player;
    }

    public void run() {
        sendImage();
    }

    private boolean checkPermission() {
        if (!motd.getBoolean("Picture.Require Permission", Boolean.valueOf(true)))
            return true;
        return this.player.hasPermission("messagehandler.motd.picture");
    }

    private ImageMessage getMessage() {
        return this.pictureUtil.createPictureMessage(this.player, Utility.colorizeList(motd.getStringList("Motd.Message"), player));
    }

    private void sendImage() {
        if (!checkPermission())
            return;
        ImageMessage pictureMessage = getMessage();
        if (pictureMessage == null)
            return;
        if (motd.getBoolean("Picture.Player Only", Boolean.valueOf(true))) {
            if (motd.getBoolean("Picture.Clear Chat", Boolean.valueOf(false)))
                this.pictureUtil.clearChat(this.player);
            pictureMessage.sendToPlayer(this.player);
            return;
        }
        this.pictureUtil.sendOutPictureMessage(pictureMessage);
    }
}
