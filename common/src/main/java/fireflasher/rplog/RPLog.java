package fireflasher.rplog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RPLog {
    public static Logger LOGGER = LogManager.getLogger("RPLog");

    public static void init() {
        #if MC_1_18_2
        LOGGER.info("This is Minecraft version 1.18.2!");
        #elif MC_1_19_2
        LOGGER.info("This is Minecraft version 1.19.2!");
        #elif MC_1_20_1
        LOGGER.info("This is Minecraft version 1.20.1!");
        #endif
    }
}
