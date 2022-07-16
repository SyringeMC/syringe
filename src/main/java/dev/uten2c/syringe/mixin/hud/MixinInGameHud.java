package dev.uten2c.syringe.mixin.hud;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.uten2c.syringe.SyringeMod;
import dev.uten2c.syringe.hud.HudPart;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud extends DrawableHelper {
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"))
    private boolean renderHotbar(InGameHud instance, float tickDelta, MatrixStack matrices) {
        return !SyringeMod.hidedHudParts.contains(HudPart.HOTBAR);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private boolean renderCrosshair(InGameHud instance, MatrixStack matrices) {
        return !SyringeMod.hidedHudParts.contains(HudPart.CROSSHAIR);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private boolean renderBossBar(BossBarHud instance, MatrixStack matrices) {
        return !SyringeMod.hidedHudParts.contains(HudPart.BOSS_BAR);
    }

    @WrapWithCondition(
        method = "renderStatusBars",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getArmor()I"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V")
        )
    )
    private boolean renderArmorBar(InGameHud instance, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        return !SyringeMod.hidedHudParts.contains(HudPart.ARMOR_BAR);
    }

    @WrapWithCondition(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"))
    private boolean renderHealthBar(InGameHud instance, MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
        return !SyringeMod.hidedHudParts.contains(HudPart.HEALTH_BAR);
    }

    @WrapWithCondition(
        method = "renderStatusBars",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;getSaturationLevel()F"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMaxAir()I")
        )
    )
    private boolean renderHungerBar(InGameHud instance, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        return !SyringeMod.hidedHudParts.contains(HudPart.HUNGER_BAR);
    }

    @WrapWithCondition(
        method = "renderStatusBars",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAir()I"),
            to = @At("TAIL")
        )
    )
    private boolean renderAirBar(InGameHud instance, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        return !SyringeMod.hidedHudParts.contains(HudPart.AIR_BAR);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private boolean renderMountHealthBar(InGameHud instance, MatrixStack matrices) {
        return !SyringeMod.hidedHudParts.contains(HudPart.MOUNT_HEALTH_BAR);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/client/util/math/MatrixStack;I)V"))
    private boolean renderMountJumpBar(InGameHud instance, MatrixStack matrices, int x) {
        return !SyringeMod.hidedHudParts.contains(HudPart.MOUNT_JUMP_BAR);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V"))
    private boolean renderExperienceBar(InGameHud instance, MatrixStack matrices, int x) {
        return !SyringeMod.hidedHudParts.contains(HudPart.EXPERIENCE_BAR);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private boolean renderHeldItemTooltip(InGameHud instance, MatrixStack matrices) {
        return !SyringeMod.hidedHudParts.contains(HudPart.HELD_ITEM_TOOLTIP);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private boolean renderStatusEffectOverlay(InGameHud instance, MatrixStack matrices) {
        return !SyringeMod.hidedHudParts.contains(HudPart.STATUS_EFFECT_OVERLAY);
    }
}
