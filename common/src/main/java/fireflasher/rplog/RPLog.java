package fireflasher.rplog;

import fireflasher.rplog.config.DefaultConfig;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RPLog {
    public static Logger LOGGER = LogManager.getLogger("RPLog");
    public static DefaultConfig CONFIG = new DefaultConfig();
    public static Chatlogger CHATLOGGER = new Chatlogger();

    public static void init() {}

    public static String getFolder(){ return  Minecraft.getInstance().gameDirectory + "/RPLogs/";}
    public static String getConfigFolder(){ return Minecraft.getInstance().gameDirectory + "/config/";}
}
