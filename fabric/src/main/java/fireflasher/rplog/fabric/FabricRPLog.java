package fireflasher.rplog.fabric;

import net.fabricmc.api.ClientModInitializer;
import fireflasher.rplog.InitClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class FabricRPLog implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        InitClient.init();

        //register trigger for dis- and connect of server
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            FabricChatLogger.onServerConnectionStatus(true);
        }));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            FabricChatLogger.onServerConnectionStatus(false);
        });
    }
}
