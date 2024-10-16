package fireflasher.rplog.fabric.mixins;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fireflasher.rplog.Chatlogger.chatFilter;


import java.util.UUID;
import net.minecraft.client.gui.chat.StandardChatListener;

@Mixin(StandardChatListener.class)
public abstract class ChatAccessMixin {
    @Inject(method = "handle", at = @At("HEAD"))
    public void onChatMessage(ChatType type, Component message, UUID sender, CallbackInfo ci) {
        if (type == ChatType.CHAT) chatFilter(message.getString());
    }
}

