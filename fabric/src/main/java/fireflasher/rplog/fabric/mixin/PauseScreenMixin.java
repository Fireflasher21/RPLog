package fireflasher.rplog.fabric.mixin;

import fireflasher.rplog.fabric.config.screens.Optionsscreen_1_18_2;
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



@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {


    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = ("createPauseMenu"), at = @At("HEAD"))
    public void createPauseMenu(CallbackInfo callbackInfo){
        if(!FabricLoader.getInstance().isModLoaded("modmenu")) {
            Button accessModOption = new Button(0, 0, 35, 20, Component.nullToEmpty("RPL"), button -> {
                Minecraft.getInstance().setScreen(new Optionsscreen_1_18_2(this));
            });
            addRenderableWidget(accessModOption);
        }
    }
}
