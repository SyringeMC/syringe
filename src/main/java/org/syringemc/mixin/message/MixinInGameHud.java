package org.syringemc.mixin.message;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.syringemc.message.MessagePosition;
import org.syringemc.message.SyringeMultilineText;
import org.syringemc.network.SyringeNetworking;

import java.util.HashSet;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    private void renderMessage(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        syringe$renderMessage(matrices);
    }

    private void syringe$renderMessage(MatrixStack matrices) {
        var deleteIds = new HashSet<String>();
        SyringeNetworking.MESSAGES.forEach((id, instance) -> {
            var ctx = instance.getContext();
            var client = MinecraftClient.getInstance();
            var textRenderer = getTextRenderer();
            var multiline = SyringeMultilineText.create(textRenderer, ctx.message());
            var x = 0.0;
            if (ctx.position() == MessagePosition.TOP_CENTER || ctx.position() == MessagePosition.MIDDLE_CENTER || ctx.position() == MessagePosition.BOTTOM_CENTER) {
                x = client.getWindow().getScaledWidth() / 2.0;
            } else if (ctx.position() == MessagePosition.TOP_RIGHT || ctx.position() == MessagePosition.MIDDLE_RIGHT || ctx.position() == MessagePosition.BOTTOM_RIGHT) {
                x = client.getWindow().getScaledWidth() - multiline.maxWidth() * ctx.size();
            }
            var y = 0.0;
            if (ctx.position() == MessagePosition.MIDDLE_LEFT || ctx.position() == MessagePosition.MIDDLE_CENTER || ctx.position() == MessagePosition.MIDDLE_RIGHT) {
                y = client.getWindow().getScaledHeight() / 2.0 - textRenderer.fontHeight * multiline.count() * ctx.size() / 2.0;
            } else if (ctx.position() == MessagePosition.BOTTOM_LEFT || ctx.position() == MessagePosition.BOTTOM_CENTER || ctx.position() == MessagePosition.BOTTOM_RIGHT) {
                y = client.getWindow().getScaledHeight() - textRenderer.fontHeight * multiline.count() * ctx.size();
            }
            matrices.push();
            matrices.translate(ctx.offsetX() + x, ctx.offsetY() + y, 0);
            matrices.scale(ctx.size(), ctx.size(), ctx.size());
            var currentTime = System.currentTimeMillis();
            var endTime = instance.getEndTime();
            var fadeOutTime = instance.getFadeOutTime();
            var alpha = 0;
            if (endTime != null && fadeOutTime != null) {
                alpha = (int) Math.floor(255 * Math.max(0f, (float) (fadeOutTime - currentTime) / (float) (fadeOutTime - endTime)));
            } else {
                alpha = (int) Math.floor(255 * Math.min(1f, 1f - (float) (instance.getFadeInTime() - currentTime) / (float) (instance.getFadeInTime() - instance.getStartTime())));
            }
            alpha = Math.max(4, alpha);
            var color = 16777215 | alpha << 24 & -16777216;
            if (ctx.position() == MessagePosition.TOP_CENTER || ctx.position() == MessagePosition.MIDDLE_CENTER || ctx.position() == MessagePosition.BOTTOM_CENTER) {
                if (ctx.shadow()) {
                    multiline.drawCenterWithShadow(matrices, 0, 0, ctx.lineHeight(), color);
                } else {
                    multiline.drawCenter(matrices, 0, 0, ctx.lineHeight(), color);
                }
            } else {
                if (ctx.shadow()) {
                    multiline.drawWithShadow(matrices, 0, 0, ctx.lineHeight(), color);
                } else {
                    multiline.draw(matrices, 0, 0, ctx.lineHeight(), color);
                }
            }
            matrices.pop();

            if (fadeOutTime != null && fadeOutTime < currentTime) {
                deleteIds.add(id);
            }
        });
        deleteIds.forEach(SyringeNetworking.MESSAGES::remove);
    }
}
