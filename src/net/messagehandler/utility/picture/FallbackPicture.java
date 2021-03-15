package net.messagehandler.utility.picture;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.Utility;

import java.io.File;

public class FallbackPicture {

    public FallbackPicture() {
    }

    public File get() {
        String FALLBACK_PATH = MessageHandler.getInstance().getDataFolder() + File.separator + "fallback.png";
        File image = new File(FALLBACK_PATH);
        if (!image.exists())
            MessageHandler.getInstance().saveResource("fallback.png", false);
        if (Utility.getConfigByFile("settings/motd.yml", FileUtilType.DEFAULT).getBoolean("Picture.Fallback")) {
            image = new File(FALLBACK_PATH);
        } else {
            image = null;
        }
        return image;
    }
}
