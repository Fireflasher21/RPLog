package fireflasher.rplog.fabric;

import net.fabricmc.api.ClientModInitializer;
import fireflasher.rplog.InitClient;

public class FabricRPLog implements ClientModInitializer{

    @Override
    public void onInitializeClient() {InitClient.init();}
}
