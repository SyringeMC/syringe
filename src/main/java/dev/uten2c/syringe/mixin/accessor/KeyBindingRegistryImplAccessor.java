package dev.uten2c.syringe.mixin.accessor;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(KeyBindingRegistryImpl.class)
public interface KeyBindingRegistryImplAccessor {
    @Accessor(value = "moddedKeyBindings", remap = false)
    static List<KeyBinding> getModdedKeyBindings() {
        throw new AssertionError();
    }
}
