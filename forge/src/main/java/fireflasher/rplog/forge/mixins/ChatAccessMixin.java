package fireflasher.rplog.forge.mixins;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fireflasher.rplog.Chatlogger.chatFilter;

@Mixin(ChatComponent.class)
public abstract class ChatAccessMixin {

    @Inject(method = ("logChatMessage"), at = @At("HEAD"))
    public void logChatMessage(GuiMessage message, CallbackInfo ci){
        chatFilter(message.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }
}

