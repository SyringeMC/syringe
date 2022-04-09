package org.syringemc.keybinding;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.syringemc.io.SaveDataManager;
import org.syringemc.mixin.accessor.KeyBindingRegistryImplAccessor;
import org.syringemc.network.SyringeNetworking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class KeyBindingManager {
    private static final List<SyringeKeyBinding> KEY_BINDINGS = new ArrayList<>();
    private static final HashMap<SyringeKeyBinding, Integer> LAST_PRESSED_TICK = new HashMap<>();

    private KeyBindingManager() {
    }

    public static void setup() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> KEY_BINDINGS.forEach(keybinding -> {
            var server = MinecraftClient.getInstance().getServer();
            if (server == null) {
                return;
            }
            var ticks = MinecraftClient.getInstance().getServer().getTicks();
            if (keybinding.isPressed()) {
                if (1 < ticks - LAST_PRESSED_TICK.getOrDefault(keybinding, 0)) {
                    pressed(keybinding);
                }
                LAST_PRESSED_TICK.put(keybinding, ticks);
            } else {
                var lastPressedTick = LAST_PRESSED_TICK.getOrDefault(keybinding, 0);
                if (0 < lastPressedTick) {
                    released(keybinding);
                    LAST_PRESSED_TICK.remove(keybinding);
                }
            }
        }));
    }

    public static void register(SyringeKeyBinding keybinding) {
        KeyBindingHelper.registerKeyBinding(keybinding);
        KEY_BINDINGS.add(keybinding);
        var client = MinecraftClient.getInstance();
        client.options.allKeys = KeyBindingRegistryImpl.process(client.options.allKeys);

        SaveDataManager.getInt(keybinding.id.toString())
            .ifPresent(code -> keybinding.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(code), false));

        KeyBinding.updateKeysByCode();
    }

    public static void reset() {
        var client = MinecraftClient.getInstance();
        var newList = Lists.newArrayList(client.options.allKeys);
        KEY_BINDINGS.forEach(keybinding -> {
            newList.remove(keybinding);
            KeyBindingRegistryImplAccessor.getModdedKeyBindings().remove(keybinding);
        });
        KEY_BINDINGS.clear();
        client.options.allKeys = newList.toArray(new KeyBinding[0]);
    }

    private static void pressed(SyringeKeyBinding keyBinding) {
        SyringeNetworking.sendKeyPressedPacket(keyBinding);
    }

    private static void released(SyringeKeyBinding keyBinding) {
        SyringeNetworking.sendKeyReleasedPacket(keyBinding);
    }
}
