package fireflasher.rplog;

public class InitClient {
    public static void init() {
        // Code here will only run on the physical client.
        // So here you can use net.minecraft.client.

        RPLog.CONFIG.setup();
        RPLog.CHATLOGGER.setup();

        RPLog.init();
    }
}
