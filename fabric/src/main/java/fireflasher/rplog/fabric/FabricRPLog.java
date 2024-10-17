package fireflasher.rplog.fabric;

import fireflasher.rplog.ChatLogManager;
import net.fabricmc.api.ClientModInitializer;
import fireflasher.rplog.InitClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.impl.client.event.lifecycle.ClientLifecycleEventsImpl;

public class FabricRPLog implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        InitClient.init();

        initializeClientEvents();
    }

    private void initializeClientEvents(){
        //register trigger for dis- and connect of server
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            ChatLogManager.onClientConnectionStatus(true);
        }));
        //TODO find a way to check on server disconnect
    }
}
