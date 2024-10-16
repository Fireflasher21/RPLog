package fireflasher.rplog.fabric.mixins;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import fireflasher.rplog.config.screens.options.*;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {

    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = ("createPauseMenu"), at = @At("HEAD"))
    public void createPauseMenu(CallbackInfo callbackInfo){
        if(!FabricLoader.getInstance().isModLoaded("modmenu")) {
            Button accessModOption = Optionsscreen.buttonBuilder(Component.nullToEmpty("RPL"),
                    0,0,35,20,
                    button -> {
                        Minecraft.getInstance().setScreen(new Optionsscreen(this));
                    });

            addRenderableWidget(accessModOption);
        }
    }
}
