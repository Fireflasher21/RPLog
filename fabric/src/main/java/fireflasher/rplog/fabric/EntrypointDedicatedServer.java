package fireflasher.rplog.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import fireflasher.rplog.InitDedicatedServer;

public class EntrypointDedicatedServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        InitDedicatedServer.init();
    }
}
