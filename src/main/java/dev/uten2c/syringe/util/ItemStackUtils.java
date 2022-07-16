package dev.uten2c.syringe.util;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemStackUtils {
    private static final String SYRINGE_NBT_ROOT = "__syringe__";

    private ItemStackUtils() {
    }

    public static boolean getFlag(@NotNull ItemStack stack, @NotNull ItemFlag flag) {
        return getBoolean(stack, flag.ordinal());
    }

    public static @Nullable ItemStack getThirdPersonStack(@NotNull ItemStack stack) {
        var nbt = stack.getSubNbt(SYRINGE_NBT_ROOT);
        if (nbt == null) {
            return null;
        }
        var thirdPersonStackNbt = nbt.getCompound("ThirdPersonStack");
        if (thirdPersonStackNbt == null) {
            return null;
        }
        var thirdPersonStack = ItemStack.fromNbt(thirdPersonStackNbt);
        if (thirdPersonStack.isEmpty()) {
            return null;
        }
        return thirdPersonStack;
    }

    private static boolean getBoolean(@NotNull ItemStack stack, int index) {
        var nbt = stack.getSubNbt(SYRINGE_NBT_ROOT);
        if (nbt == null) {
            return false;
        }

        return (nbt.getInt("Flags") >> index & 1) == 1;
    }
}
