package org.syringemc.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.Perspective;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.syringemc.SyringeMod;
import org.syringemc.keybinding.KeyBindingManager;
import org.syringemc.keybinding.KeyCode;
import org.syringemc.keybinding.SyringeKeyBinding;
import org.syringemc.message.MessageContext;
import org.syringemc.message.MessageInstance;
import org.syringemc.message.MessagePosition;
import org.syringemc.util.ExtendedGameOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class SyringeNetworking {
    private static final String NAMESPACE = "syringe";
    public static final Map<String, MessageInstance> MESSAGES = new HashMap<>();

    // S2C
    public static final Identifier MESSAGE_DISPLAY_ID = new Identifier(NAMESPACE, "message/display");
    public static final Identifier MESSAGE_DISCARD_ID = new Identifier(NAMESPACE, "message/discard");
    public static final Identifier MESSAGE_CLEAR_ID = new Identifier(NAMESPACE, "message/clear");
    public static final Identifier KEYBINDING_REGISTER_ID = new Identifier(NAMESPACE, "keybinding/register");
    public static final Identifier PERSPECTIVE_SET_ID = new Identifier(NAMESPACE, "perspective/set");
    public static final Identifier PERSPECTIVE_LOCK_ID = new Identifier(NAMESPACE, "perspective/lock");

    // C2S
    public static final Identifier HANDSHAKE_ID = new Identifier(NAMESPACE, "handshake");
    public static final Identifier KEYBINDING_PRESSED_ID = new Identifier(NAMESPACE, "keybinding/pressed");
    public static final Identifier KEYBINDING_RELEASED_ID = new Identifier(NAMESPACE, "keybinding/released");

    private SyringeNetworking() {
    }

    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(MESSAGE_DISPLAY_ID, SyringeNetworking::displayMessage);
        ClientPlayNetworking.registerGlobalReceiver(MESSAGE_DISCARD_ID, SyringeNetworking::discardMessage);
        ClientPlayNetworking.registerGlobalReceiver(MESSAGE_CLEAR_ID, SyringeNetworking::clearMessages);
        ClientPlayNetworking.registerGlobalReceiver(KEYBINDING_REGISTER_ID, SyringeNetworking::registerKeybindings);
        ClientPlayNetworking.registerGlobalReceiver(PERSPECTIVE_SET_ID, SyringeNetworking::setPerspective);
        ClientPlayNetworking.registerGlobalReceiver(PERSPECTIVE_LOCK_ID, SyringeNetworking::lockPerspective);
    }

    private static void displayMessage(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var id = buf.readString();
        var message = buf.readText();
        var shadow = buf.readBoolean();
        var size = buf.readFloat();
        var lineHeight = buf.readInt();
        var position = buf.readEnumConstant(MessagePosition.class);
        var offsetX = buf.readInt();
        var offsetY = buf.readInt();
        var fadeIn = buf.readLong();

        if (size <= 0) {
            return;
        }
        var context = new MessageContext(message, shadow, size, lineHeight, position, offsetX, offsetY);
        var instance = new MessageInstance(context, System.currentTimeMillis(), System.currentTimeMillis() + fadeIn);
        MESSAGES.put(id, instance);
    }

    private static void discardMessage(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var id = buf.readString();
        var fadeOut = buf.readLong();

        var instance = MESSAGES.get(id);
        if (instance != null) {
            instance.setEndTime(System.currentTimeMillis());
            instance.setFadeOutTime(System.currentTimeMillis() + fadeOut);
        }
    }

    private static void clearMessages(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        MESSAGES.clear();
    }

    private static void registerKeybindings(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        buf.readList(buf1 -> new Pair<>(buf1.readIdentifier(), buf1.readEnumConstant(KeyCode.class)))
            .forEach(entry -> KeyBindingManager.register(new SyringeKeyBinding(entry.getLeft(), entry.getRight())));
    }

    private static void setPerspective(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var id = buf.readInt();
        Arrays.stream(Perspective.values())
            .filter(p -> p.ordinal() == id)
            .findFirst()
            .ifPresent(p -> ((ExtendedGameOptions) client.options).setPerspective(p, true));
    }

    private static void lockPerspective(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        SyringeMod.isPerspectiveLocked = buf.readBoolean();
    }

    public static void sendKeyPressedPacket(SyringeKeyBinding keyBinding) {
        var buf = PacketByteBufs.create();
        buf.writeIdentifier(keyBinding.id);
        ClientPlayNetworking.send(KEYBINDING_PRESSED_ID, buf);
    }

    public static void sendKeyReleasedPacket(SyringeKeyBinding keyBinding) {
        var buf = PacketByteBufs.create();
        buf.writeIdentifier(keyBinding.id);
        ClientPlayNetworking.send(KEYBINDING_RELEASED_ID, buf);
    }
}
