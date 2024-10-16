package fireflasher.rplog;

import fireflasher.rplog.config.DefaultConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.HashMap;
import java.util.List;


public class RPLog {
    public static Logger LOGGER = LogManager.getLogger("RPLog");
    public static DefaultConfig CONFIG = new DefaultConfig();
    public static Chatlogger CHATLOGGER = new Chatlogger();

    public static HashMap<String,Component> translateAbleStrings = new HashMap<>();



    public static void init() {
        initLanguageFileTranslations();
    }

    private static void initLanguageFileTranslations(){
        List<String> keys = List.of(
                "rplog.config.screen.defaults",
                "rplog.config.screen.done",
                "rplog.config.screen.delete",
                "rplog.config.optionscreen.title",
                "rplog.config.optionscreen.add_Server",
                "rplog.config.optionscreen.open_LogFolder",
                "rplog.config.optionscreen.configuration_Servers",
                "rplog.config.optionscreen.delete_Servers",
                "rplog.config.optionscreen.verification.delete",
                "rplog.config.optionscreen.verification.cancel",
                "rplog.config.optionscreen.verification.message",
                "rplog.config.serverscreen.reset_defaults",
                "rplog.config.serverscreen.add_Keywords",

                "rplog.screens.optionscreen.openfolder_error",
                "rplog.logger.defaultconfig.config_created",
                "rplog.logger.defaultconfig.config_create_error",
                "rplog.logger.chatlogger.zip_warning",
                "rplog.logger.chatlogger.write_warning"

        );
        #if MC_1_18_2
        for (String key :keys) {
            translateAbleStrings.put(key, new net.minecraft.network.chat.TranslatableComponent(key));
        }
        #elif MC_1_19_4 || MC_1_20_1 || MC_1_20_6
        for (String key :keys) {
            translateAbleStrings.put(key,Component.translatable(key));
        }
        #endif

    }

    public static String getFolder(){ return  Minecraft.getInstance().gameDirectory + "/RPLogs/";}
    public static String getConfigFolder(){ return Minecraft.getInstance().gameDirectory + "/config/";}
}
