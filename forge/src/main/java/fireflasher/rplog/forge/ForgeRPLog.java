package fireflasher.rplog.forge;

import fireflasher.rplog.Chatlogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.BiFunction;
import fireflasher.rplog.config.screens.options.*;
import net.minecraftforge.fmlclient.ConfigGuiHandler;


@Mod("rplog")
public class ForgeRPLog {
    public ForgeRPLog() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);


        MinecraftForge.EVENT_BUS.register(this);
        registerConfigScreen();

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EntrypointClient::init);
    }

    private void registerConfigScreen(){
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory(new BiFunction<Minecraft, Screen, Screen>() {
                    @Override
                    public Screen apply(Minecraft mc, Screen screen) {
                        return new Optionsscreen(Minecraft.getInstance().screen);
                    }
                }));
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        // This event is triggered when the player logs in
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener handler = mc.getConnection();

        if (handler != null) {
            Chatlogger.onClientConnectionStatus(true);
        }
    }
    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        // This event is triggered when the player logs in
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener handler = mc.getConnection();

        if (handler != null) {
            Chatlogger.onClientConnectionStatus(false);
        }
    }


    private void setup(final FMLCommonSetupEvent event) {}
    private void doClientStuff(final FMLClientSetupEvent event){}
}
