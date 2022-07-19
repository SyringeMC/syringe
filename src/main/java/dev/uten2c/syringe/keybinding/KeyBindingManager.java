package dev.uten2c.syringe.keybinding;

import com.google.common.collect.Lists;
import dev.uten2c.syringe.SyringeMod;
import dev.uten2c.syringe.io.SaveDataManager;
import dev.uten2c.syringe.mixin.accessor.KeyBindingRegistryImplAccessor;
import dev.uten2c.syringe.network.SyringeNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class KeyBindingManager {
    private static final Set<KeybindingEntry> KEY_BINDING_ENTRIES = new HashSet<>();
    private static final Set<KeybindingEntry> PRESSING_KEY_ENTRIES = new HashSet<>();

    private KeyBindingManager() {
    }

    public static void setup() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!SyringeMod.isInSyringeServer) {
                return;
            }
            KEY_BINDING_ENTRIES.forEach(entry -> {
                if (entry.keyBinding().isPressed()) {
                    if (!PRESSING_KEY_ENTRIES.contains(entry)) {
                        PRESSING_KEY_ENTRIES.add(entry);
                        pressed(entry);
                    }
                } else {
                    if (PRESSING_KEY_ENTRIES.contains(entry)) {
                        PRESSING_KEY_ENTRIES.remove(entry);
                        released(entry);
                    }
                }
            });
        });
    }

    public static void register(@NotNull KeybindingEntry entry) {
        var keyBinding = entry.keyBinding();
        if (keyBinding instanceof SyringeKeyBinding syringeKeybinding) {
            KeyBindingHelper.registerKeyBinding(syringeKeybinding);
            SaveDataManager.getInt(entry.id().toTranslationKey("key"))
                .ifPresent(code -> syringeKeybinding.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(code), false));
        }

        KEY_BINDING_ENTRIES.add(entry);
        var client = MinecraftClient.getInstance();
        client.options.allKeys = KeyBindingRegistryImpl.process(client.options.allKeys);
        KeyBinding.updateKeysByCode();
    }

    public static void reset() {
        var client = MinecraftClient.getInstance();
        var newList = Lists.newArrayList(client.options.allKeys);
        KEY_BINDING_ENTRIES.stream()
            .filter(entry -> entry.keyBinding() instanceof SyringeKeyBinding)
            .map(entry -> (SyringeKeyBinding) entry.keyBinding())
            .forEach(keyBinding -> {
                newList.remove(keyBinding);
                KeyBindingRegistryImplAccessor.getModdedKeyBindings().remove(keyBinding);
            });
        KEY_BINDING_ENTRIES.clear();
        client.options.allKeys = newList.toArray(new KeyBinding[0]);
    }

    private static void pressed(KeybindingEntry entry) {
        SyringeNetworking.sendKeyPressedPacket(entry);
    }

    private static void released(KeybindingEntry entry) {
        SyringeNetworking.sendKeyReleasedPacket(entry);
    }
}
