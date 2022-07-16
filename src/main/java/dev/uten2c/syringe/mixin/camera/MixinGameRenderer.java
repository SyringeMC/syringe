package dev.uten2c.syringe.mixin.camera;

import dev.uten2c.syringe.SyringeMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    private MinecraftClient client;
    private double syringe$lastZoomMultiplier;

    @Inject(method = "getFov", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (!client.options.getPerspective().isFirstPerson()) {
            return;
        }
        var value = MathHelper.lerp(tickDelta, syringe$lastZoomMultiplier, SyringeMod.zoom);
        syringe$lastZoomMultiplier = value;
        cir.setReturnValue(cir.getReturnValueD() * value);
    }
}
