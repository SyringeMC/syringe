package dev.uten2c.syringe.mixin.item;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.uten2c.syringe.util.ItemFlag;
import dev.uten2c.syringe.util.ItemStackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    private MinecraftClient client;

    @WrapWithCondition(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    private boolean disableBobView(GameRenderer instance, MatrixStack matrices, float tickDelta) {
        var cameraEntity = client.getCameraEntity();
        if (cameraEntity instanceof LivingEntity livingEntity) {
            var stack = livingEntity.getMainHandStack();
            return !ItemStackUtils.getFlag(stack, ItemFlag.DO_NOT_BOBBING);
        }
        return true;
    }
}
