package fireflasher.rplog.fabric.mixin;


import fireflasher.rplog.fabric.FabricChatLogger;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

#if MC_1_18_2
import java.util.UUID;
import net.minecraft.client.gui.chat.StandardChatListener;
@Mixin(StandardChatListener.class)
public abstract class ChatAccessMixin {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatType type, Component message, UUID sender, CallbackInfo ci) {
        if(type == ChatType.CHAT) FabricChatLogger.chatFilter(message.getString());
    }
}
#elif MC_1_19_2
import net.minecraft.network.chat.PlayerChatMessage;

@Mixin(ChatListener.class)
public abstract class ChatAccessMixin {
    @Inject(method = "handleChatMessage", at = @At("HEAD"),cancellable = true)
    public void onChatMessage(PlayerChatMessage chatMessage, ChatType.Bound bound, CallbackInfo ci) {
        if( bound.chatType().equals(ChatType.CHAT)) FabricChatLogger.chatFilter(chatMessage.signedContent().toString());
    }
}


#elif MC_1_20_4
import net.minecraft.client.multiplayer.chat.ChatListener;

import java.util.function.BooleanSupplier;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import com.mojang.authlib.GameProfile;

@Mixin(ChatListener.class)
public abstract class ChatAccessMixin {

    @Inject(method = "logPlayerMessage", at = @At("HEAD"),cancellable = true)
    public void onChatMessagelogPlayerMessage(PlayerChatMessage chatMessage, ChatType.Bound bound, GameProfile gameProfile, ChatTrustLevel trustLevel, CallbackInfo ci) {
        if( bound.chatType().equals(ChatType.CHAT)) FabricChatLogger.chatFilter(chatMessage.signedContent());
    }
}

#endif

