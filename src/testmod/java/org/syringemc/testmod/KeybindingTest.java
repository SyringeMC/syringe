package org.syringemc.testmod;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.syringemc.keybinding.KeyCode;
import org.syringemc.network.SyringeNetworking;

import java.util.List;

public final class KeybindingTest {
    private static final List<Pair<Identifier, KeyCode>> KEY_BINDINGS = List.of(
        new Pair<>(new Identifier("testmod", "test1"), KeyCode.R),
        new Pair<>(new Identifier("testmod", "test2"), KeyCode.C)
    );

    private KeybindingTest() {
    }

    public static void startListener() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> registerKeyBinding(handler.player));

        ServerPlayNetworking.registerGlobalReceiver(SyringeNetworking.KEYBINDING_PRESSED_ID, (server, player, handler, buf, responseSender) -> {
            var id = buf.readIdentifier();
            player.sendMessage(new LiteralText("Pressed: " + id), false);
        });

        ServerPlayNetworking.registerGlobalReceiver(SyringeNetworking.KEYBINDING_RELEASED_ID, (server, player, handler, buf, responseSender) -> {
            var id = buf.readIdentifier();
            player.sendMessage(new LiteralText("Released: " + id), false);
        });
    }

    private static void registerKeyBinding(ServerPlayerEntity player) {
        var buf = PacketByteBufs.create();
        buf.writeCollection(KEY_BINDINGS, (buf1, pair) -> {
            buf1.writeIdentifier(pair.getLeft());
            buf1.writeEnumConstant(pair.getRight());
        });
        ServerPlayNetworking.send(player, SyringeNetworking.KEYBINDING_REGISTER_ID, buf);
    }
}
