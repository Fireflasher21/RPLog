package fireflasher.rplog.fabric;

import fireflasher.rplog.ChatLogManager;
import fireflasher.rplog.RPLog;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class FabricRPLog implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        RPLog.init();

        initializeClientEvents();
    }

    private void initializeClientEvents(){
        //register trigger for dis- and connect of server
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            ChatLogManager.onClientConnectionStatus(true);
        }));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ChatLogManager.onClientConnectionStatus(false);
        });

    }
}
