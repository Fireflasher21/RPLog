package fireflasher.rplog.fabric;

import fireflasher.rplog.ExampleMod;
import net.fabricmc.api.ModInitializer;

public class Entrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        // Code here will run on both physical client and server.
        // Client classes may or may not be available - be careful!
        ExampleMod.LOGGER.info("Hello, Fabric!");
        ExampleMod.init();
    }
}
