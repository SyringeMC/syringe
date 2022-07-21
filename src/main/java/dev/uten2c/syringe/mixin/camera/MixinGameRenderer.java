package dev.uten2c.syringe.mixin.camera;

import dev.uten2c.syringe.SyringeMod;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    private float fovMultiplier;

    @Inject(method = "updateFovMultiplier", at = @At("TAIL"))
    private void zoom(CallbackInfo ci) {
        fovMultiplier *= SyringeMod.zoom;
    }
}
