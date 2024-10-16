package fireflasher.rplog.fabric.mixins;

import net.minecraft.network.chat.ChatType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fireflasher.rplog.Chatlogger.chatFilter;

import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.PlayerChatMessage;

@Mixin(ChatListener.class)
public abstract class ChatAccessMixin {
    @Inject(method = "logPlayerMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(PlayerChatMessage chatMessage, ChatType.Bound bound, GameProfile gameProfile, ChatTrustLevel trustLevel, CallbackInfo ci) {
        if (bound.chatType().equals(ChatType.CHAT)) chatFilter(chatMessage.signedContent());
    }
}

