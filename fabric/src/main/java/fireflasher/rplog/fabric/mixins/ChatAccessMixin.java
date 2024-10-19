package fireflasher.rplog.fabric.mixins;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fireflasher.rplog.Chatlogger.chatFilter;

#if MC_1_18_2
import java.util.UUID;
import net.minecraft.client.gui.chat.StandardChatListener;

@Mixin(StandardChatListener.class)
public abstract class ChatAccessMixin {
    @Inject(method = "handle", at = @At("HEAD"))
    public void onChatMessage(ChatType type, Component message, UUID sender, CallbackInfo ci) {
        if (type == ChatType.CHAT) chatFilter(message.getString());
    }
}
#else
import net.minecraft.client.multiplayer.chat.ChatListener;
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


#endif
