package fireflasher.rplog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fireflasher.rplog.config.DefaultConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
