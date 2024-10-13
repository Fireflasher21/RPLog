package fireflasher.rplog.fabric;

import net.fabricmc.api.ClientModInitializer;
import fireflasher.rplog.InitClient;

public class EntrypointClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InitClient.init();
    }
}
