package fireflasher.rplog.forge.mixin;

import fireflasher.rplog.config.screens.options.*;
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

    protected PauseScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    #if MC_1_18_2 || MC_1_19_2
    @Inject(method = ("createPauseMenu"), at = @At("HEAD"))
    public void createPauseMenu(CallbackInfo callbackInfo){
        Button accessModOption = new Button(0, 0, 35, 20, Component.nullToEmpty("RPL"), button -> {
            Minecraft.getInstance().setScreen(new Optionsscreen_1_18_2(this));
        });
        addRenderableWidget(accessModOption);
    }
    #elif MC_1_20_1 || MC_1_20_4 || MC_1_20_6
    @Inject(method = ("createPauseMenu"), at = @At("HEAD"))
    public void createPauseMenu(CallbackInfo callbackInfo){
        Button accessModOption = Button.builder(Component.nullToEmpty("RPL"), button -> {
            Minecraft.getInstance().setScreen(new Optionsscreen_1_20_1(this));
        }).bounds(0, 0, 35, 20).build();
        addRenderableWidget(accessModOption);
    }
    #endif
}
