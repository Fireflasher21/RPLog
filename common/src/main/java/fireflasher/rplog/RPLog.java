package fireflasher.rplog;

import fireflasher.rplog.config.DefaultConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;


public class RPLog{
    public static Logger LOGGER = LogManager.getLogger("RPLog");
    public static DefaultConfig CONFIG = new DefaultConfig();
    public static ChatLogManager ChatLogManager = new ChatLogManager();

    public static HashMap<String,Component> translateAbleStrings = new HashMap<>();


    public static void init() {
        CONFIG.setup();
        ChatLogManager.setup();

        loadLanguageFile();
    }

    private static void loadLanguageFile(){
        for(String key: translateAbleStrings.keySet()){
            translateAbleStrings.put(key,Component.translatable(key));
        }
    }


    public static String getFolder(){ return  Minecraft.getInstance().gameDirectory + "/RPLogs/";}
    public static String getConfigFolder(){ return Minecraft.getInstance().gameDirectory + "/config/";}

}
