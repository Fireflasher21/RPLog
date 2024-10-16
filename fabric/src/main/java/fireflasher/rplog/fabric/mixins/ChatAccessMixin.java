package fireflasher.rplog.fabric.mixins;

import net.minecraft.network.chat.ChatType;
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
#elif MC_1_19_2
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.client.multiplayer.chat.ChatListener;

@Mixin(ChatListener.class)
public abstract class ChatAccessMixin {
    @Inject(method = "handleChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(PlayerChatMessage chatMessage, ChatType.Bound bound, CallbackInfo ci) {
        if (bound.chatType().equals(ChatType.CHAT)) chatFilter(chatMessage.signedContent().toString());
    }
}
#elif MC_1_20_1 || MC_1_20_4 || MC_1_20_6
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


#endif
