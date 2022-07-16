package dev.uten2c.syringe.keybinding;

import com.google.common.collect.Lists;
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
    private static final Set<KeyBinding> KEY_BINDINGS = new HashSet<>();
    private static final Set<KeyBinding> PRESSING_KEYS = new HashSet<>();

    private KeyBindingManager() {
    }

    public static void setup() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var server = MinecraftClient.getInstance().getServer();
            if (server == null) {
                return;
            }
            KEY_BINDINGS.forEach(keybinding -> {
                if (keybinding.isPressed()) {
                    if (!PRESSING_KEYS.contains(keybinding)) {
                        PRESSING_KEYS.add(keybinding);
                        pressed(keybinding);
                    }
                } else {
                    if (PRESSING_KEYS.contains(keybinding)) {
                        PRESSING_KEYS.remove(keybinding);
                        released(keybinding);
                    }
                }
            });
        });
    }

    public static void register(@NotNull String translateKey, @NotNull KeyCode defaultKey) {
        var keybinding = KeyBinding.KEYS_BY_ID.get(translateKey);
        if (keybinding == null) {
            keybinding = new SyringeKeyBinding(translateKey, defaultKey);
        }
        if (keybinding instanceof SyringeKeyBinding syringeKeybinding) {
            KeyBindingHelper.registerKeyBinding(syringeKeybinding);
            SaveDataManager.getInt(translateKey)
                .ifPresent(code -> syringeKeybinding.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(code), false));
        }

        KEY_BINDINGS.add(keybinding);
        var client = MinecraftClient.getInstance();
        client.options.allKeys = KeyBindingRegistryImpl.process(client.options.allKeys);
        KeyBinding.updateKeysByCode();
    }

    public static void reset() {
        var client = MinecraftClient.getInstance();
        var newList = Lists.newArrayList(client.options.allKeys);
        KEY_BINDINGS.stream()
            .filter(keyBinding -> keyBinding instanceof SyringeKeyBinding)
            .forEach(keyBinding -> {
                newList.remove(keyBinding);
                KeyBindingRegistryImplAccessor.getModdedKeyBindings().remove(keyBinding);
            });
        KEY_BINDINGS.clear();
        client.options.allKeys = newList.toArray(new KeyBinding[0]);
    }

    private static void pressed(KeyBinding keyBinding) {
        SyringeNetworking.sendKeyPressedPacket(keyBinding);
    }

    private static void released(KeyBinding keyBinding) {
        SyringeNetworking.sendKeyReleasedPacket(keyBinding);
    }
}
