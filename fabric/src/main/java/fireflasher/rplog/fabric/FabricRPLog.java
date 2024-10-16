package fireflasher.rplog.fabric;

import fireflasher.rplog.Chatlogger;
import net.fabricmc.api.ClientModInitializer;
import fireflasher.rplog.InitClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class FabricRPLog implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        InitClient.init();

        initializeClientEvents();
    }

    private void initializeClientEvents(){
        //register trigger for dis- and connect of server
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            Chatlogger.onClientConnectionStatus(true);
        }));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            Chatlogger.onClientConnectionStatus(true);
        });

    }
}
