package fireflasher.rplog.fabric.mixins;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fireflasher.rplog.ChatLogManager.chatFilter;

import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ChatListener.class)
public abstract class ChatAccessMixin {

    @Inject(method = "showMessageToPlayer", at = @At("HEAD"), cancellable = true)
    public void onHudUpdate(ChatType.Bound boundChatType, PlayerChatMessage chatMessage, Component decoratedServerContent, GameProfile gameProfile, boolean onlyShowSecureChat, Instant timestamp, CallbackInfoReturnable<Boolean> cir) {
        chatFilter(chatMessage.signedContent());
    }

    @Inject(method = "logSystemMessage", at = @At("HEAD"), cancellable = true)
    public void onSystemMessageLog(Component message, Instant timestamp, CallbackInfo ci) {
        chatFilter(message.getContents().toString());
    }
}

