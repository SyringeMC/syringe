package dev.uten2c.syringe.mixin.camera;

import dev.uten2c.syringe.SyringeMod;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {
    @Inject(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onUpdateMouse(DD)V"), cancellable = true)
    private void cameraLock(CallbackInfo ci) {
        if (SyringeMod.cameraLock) {
            ci.cancel();
        }
    }
}
