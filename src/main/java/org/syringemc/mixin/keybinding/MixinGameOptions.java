package org.syringemc.mixin.keybinding;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.syringemc.keybinding.SyringeKeyBinding;

@Mixin(GameOptions.class)
public class MixinGameOptions {
    @Inject(method = "setKeyCode", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;write()V"), cancellable = true)
    private void write(KeyBinding key, InputUtil.Key code, CallbackInfo ci) {
        if (key instanceof SyringeKeyBinding) {
            ci.cancel();
        }
    }
}
