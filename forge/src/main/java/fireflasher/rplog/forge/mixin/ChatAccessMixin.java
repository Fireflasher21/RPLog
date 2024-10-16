package fireflasher.rplog.forge.mixin;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fireflasher.rplog.Chatlogger.chatFilter;

#if MC_1_18_2
@Mixin(ChatComponent.class)
public abstract class ChatAccessMixin {

    @Inject(method = ("addMessage(Lnet/minecraft/network/chat/Component;I)V"), at = @At("HEAD"))
    public void logChatMessage(Component chat, int i, CallbackInfo ci){
        chatFilter(chat.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }
}
#elif MC_1_19_2
import net.minecraft.client.GuiMessageTag;

@Mixin(ChatComponent.class)
public abstract class ChatAccessMixin {

    @Inject(method = ("logChatMessage"), at = @At("HEAD"))
    public void logChatMessage(Component chat, GuiMessageTag tag, CallbackInfo ci){
        chatFilter(chat.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }
}


#elif MC_1_20_4


#endif
