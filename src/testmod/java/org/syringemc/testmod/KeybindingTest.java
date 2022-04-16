package org.syringemc.testmod;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.syringemc.keybinding.KeyCode;
import org.syringemc.network.SyringeNetworking;

import java.util.List;

public final class KeybindingTest {
    private static final List<Keybinding> KEY_BINDINGS = List.of(
        new Keybinding(new Identifier("testmod", "test1"), KeyCode.R),
        new Keybinding(new Identifier("testmod", "test2"), KeyCode.C),
        new Keybinding("key.testmod.test3", KeyCode.I),
        new Keybinding("key.attack")
    );

    private KeybindingTest() {
    }

    public static void startListener() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> registerKeyBinding(handler.player));

        ServerPlayNetworking.registerGlobalReceiver(SyringeNetworking.KEYBINDING_PRESSED_ID, (server, player, handler, buf, responseSender) -> {
            var id = buf.readString();
            player.sendMessage(new LiteralText("Pressed: " + id), false);
        });

        ServerPlayNetworking.registerGlobalReceiver(SyringeNetworking.KEYBINDING_RELEASED_ID, (server, player, handler, buf, responseSender) -> {
            var id = buf.readString();
            player.sendMessage(new LiteralText("Released: " + id), false);
        });
    }

    private static void registerKeyBinding(ServerPlayerEntity player) {
        var buf = PacketByteBufs.create();
        buf.writeCollection(KEY_BINDINGS, (buf1, entry) -> {
            buf1.writeString(entry.translateKey);
            buf1.writeEnumConstant(entry.keyCode);
        });
        ServerPlayNetworking.send(player, SyringeNetworking.KEYBINDING_REGISTER_ID, buf);
    }

    private static class Keybinding {
        private final @NotNull String translateKey;
        private final @NotNull KeyCode keyCode;

        Keybinding(Identifier id, @NotNull KeyCode keyCode) {
            this.translateKey = String.format("key.%s.%s", id.getNamespace(), id.getPath());
            this.keyCode = keyCode;
        }

        Keybinding(@NotNull String translateKey, @NotNull KeyCode keyCode) {
            this.translateKey = translateKey;
            this.keyCode = keyCode;
        }

        Keybinding(@NotNull String translateKey) {
            this.translateKey = translateKey;
            this.keyCode = KeyCode.ESCAPE;
        }
    }
}
