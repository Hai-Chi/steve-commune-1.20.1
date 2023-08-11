package top.krahsu.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.krahsu.utils.ChatUtils;

@Mixin(value = ChatHud.class, priority = 9999)
public class MixinChat {
//    @Inject(method = "logChatMessage", at = @At("HEAD"))
//    public void modifyMessage(Text message, MessageIndicator indicator, CallbackInfo ci) {
//        System.out.println("OK");
//        ChatUtils.highlightPosition(message);
//    }
}
