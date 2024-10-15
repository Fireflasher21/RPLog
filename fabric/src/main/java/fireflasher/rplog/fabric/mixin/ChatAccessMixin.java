package fireflasher.rplog.fabric.mixin;


import fireflasher.rplog.fabric.FabricChatLogger;
import net.minecraft.client.gui.chat.StandardChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(StandardChatListener.class)
public abstract class ChatAccessMixin {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatType type, Component message, UUID sender, CallbackInfo ci) {
        if(type == ChatType.CHAT) FabricChatLogger.chatFilter(message.getString());
    }
}
