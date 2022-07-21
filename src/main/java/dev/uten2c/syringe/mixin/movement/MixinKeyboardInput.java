package dev.uten2c.syringe.mixin.movement;

import dev.uten2c.syringe.SyringeMod;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void lockMovement(boolean slowDown, float f, CallbackInfo ci) {
        if (SyringeMod.movementLock) {
            ci.cancel();
        }
    }
}
