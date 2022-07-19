package dev.uten2c.syringe.keybinding;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record KeybindingEntry(@NotNull Identifier id, @NotNull KeyBinding keyBinding) {
}
