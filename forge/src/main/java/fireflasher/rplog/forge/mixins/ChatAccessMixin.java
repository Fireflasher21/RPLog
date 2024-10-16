package fireflasher.rplog.forge.mixins;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fireflasher.rplog.Chatlogger.chatFilter;

@Mixin(ChatComponent.class)
public abstract class ChatAccessMixin {

    @Inject(method = ("addMessage(Lnet/minecraft/network/chat/Component;I)V"), at = @At("HEAD"))
    public void logChatMessage(Component chat, int i, CallbackInfo ci){
        chatFilter(chat.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }
}