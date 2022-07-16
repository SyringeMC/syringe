package dev.uten2c.syringe.mixin.item;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.uten2c.syringe.util.ItemFlag;
import dev.uten2c.syringe.util.ItemStackUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen<T extends ScreenHandler> {

    @Shadow
    @Nullable
    protected abstract Slot getSlotAt(double x, double y);

    @Shadow
    protected abstract boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button);

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Shadow
    @Final
    protected T handler;

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V"))
    private void cancelDrop(HandledScreen<T> instance, Slot slot, int slotId, int button, SlotActionType actionType) {
        if (actionType == SlotActionType.THROW && ItemStackUtils.getFlag(slot.getStack(), ItemFlag.CAN_NOT_DROP)) {
            return;
        }
        instance.onMouseClick(slot, slotId, button, actionType);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void cancelDrop(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        var slot = getSlotAt(mouseX, mouseY);
        var outsideBounds = isClickOutsideBounds(mouseX, mouseY, x, y, button);
        var canNotDrop = ItemStackUtils.getFlag(handler.getCursorStack(), ItemFlag.CAN_NOT_DROP);
        if (slot == null && outsideBounds && canNotDrop) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void disableTooltip(MatrixStack matrices, int x, int y, CallbackInfo ci) {
        if (focusedSlot == null) {
            return;
        }
        var stack = focusedSlot.getStack();
        if (!ItemStackUtils.getFlag(stack, ItemFlag.DISABLE_TOOLTIP)) {
            return;
        }
        ci.cancel();
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlight(Lnet/minecraft/client/util/math/MatrixStack;III)V"))
    private boolean disableSlotHighlight(MatrixStack matrices, int x, int y, int z) {
        if (focusedSlot == null) {
            return true;
        }
        var stack = focusedSlot.getStack();
        return !ItemStackUtils.getFlag(stack, ItemFlag.DISABLE_SLOT_HIGHLIGHT);
    }
}
