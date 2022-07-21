package dev.uten2c.syringe.network;

import com.google.common.collect.Sets;
import dev.uten2c.syringe.SyringeMod;
import dev.uten2c.syringe.hud.HudPart;
import dev.uten2c.syringe.keybinding.KeyBindingManager;
import dev.uten2c.syringe.keybinding.KeyCode;
import dev.uten2c.syringe.keybinding.KeybindingEntry;
import dev.uten2c.syringe.keybinding.SyringeKeyBinding;
import dev.uten2c.syringe.message.MessageContext;
import dev.uten2c.syringe.message.MessageInstance;
import dev.uten2c.syringe.message.MessagePosition;
import dev.uten2c.syringe.util.ExtendedGameOptions;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class SyringeNetworking {
    private static final String NAMESPACE = "syringe";
    private static final Logger LOGGER = LoggerFactory.getLogger(SyringeNetworking.class);
    public static final Map<String, MessageInstance> MESSAGES = new HashMap<>();

    // Login
    public static final Identifier HANDSHAKE_ID = new Identifier(NAMESPACE, "handshake");

    // S2C
    public static final Identifier MESSAGE_DISPLAY_ID = new Identifier(NAMESPACE, "message/display");
    public static final Identifier MESSAGE_DISCARD_ID = new Identifier(NAMESPACE, "message/discard");
    public static final Identifier MESSAGE_CLEAR_ID = new Identifier(NAMESPACE, "message/clear");
    public static final Identifier KEYBINDING_REGISTER_ID = new Identifier(NAMESPACE, "keybinding/register");
    public static final Identifier PERSPECTIVE_SET_ID = new Identifier(NAMESPACE, "perspective/set");
    public static final Identifier PERSPECTIVE_LOCK_ID = new Identifier(NAMESPACE, "perspective/lock");
    public static final Identifier CAMERA_SET_DIRECTION_ID = new Identifier(NAMESPACE, "camera/set_direction");
    public static final Identifier CAMERA_ZOOM_ID = new Identifier(NAMESPACE, "camera/zoom");
    public static final Identifier CAMERA_LOCK_ID = new Identifier(NAMESPACE, "camera/lock");
    public static final Identifier HUD_HIDE_ID = new Identifier(NAMESPACE, "hud/hide");
    public static final Identifier HUD_SHOW_ID = new Identifier(NAMESPACE, "hud/show");
    public static final Identifier MOVEMENT_LOCK_ID = new Identifier(NAMESPACE, "movement/lock");

    // C2S
    public static final Identifier KEYBINDING_PRESSED_ID = new Identifier(NAMESPACE, "keybinding/pressed");
    public static final Identifier KEYBINDING_RELEASED_ID = new Identifier(NAMESPACE, "keybinding/released");

    private SyringeNetworking() {
    }

    public static void registerReceivers() {
        ClientLoginNetworking.registerGlobalReceiver(HANDSHAKE_ID, SyringeNetworking::handshake);
        ClientPlayNetworking.registerGlobalReceiver(MESSAGE_DISPLAY_ID, SyringeNetworking::displayMessage);
        ClientPlayNetworking.registerGlobalReceiver(MESSAGE_DISCARD_ID, SyringeNetworking::discardMessage);
        ClientPlayNetworking.registerGlobalReceiver(MESSAGE_CLEAR_ID, SyringeNetworking::clearMessages);
        ClientPlayNetworking.registerGlobalReceiver(KEYBINDING_REGISTER_ID, SyringeNetworking::registerKeybindings);
        ClientPlayNetworking.registerGlobalReceiver(PERSPECTIVE_SET_ID, SyringeNetworking::setPerspective);
        ClientPlayNetworking.registerGlobalReceiver(PERSPECTIVE_LOCK_ID, SyringeNetworking::lockPerspective);
        ClientPlayNetworking.registerGlobalReceiver(CAMERA_SET_DIRECTION_ID, SyringeNetworking::setCameraDirection);
        ClientPlayNetworking.registerGlobalReceiver(CAMERA_ZOOM_ID, SyringeNetworking::zoom);
        ClientPlayNetworking.registerGlobalReceiver(CAMERA_LOCK_ID, SyringeNetworking::lockCamera);
        ClientPlayNetworking.registerGlobalReceiver(HUD_HIDE_ID, SyringeNetworking::hudHide);
        ClientPlayNetworking.registerGlobalReceiver(HUD_SHOW_ID, SyringeNetworking::hudShow);
        ClientPlayNetworking.registerGlobalReceiver(MOVEMENT_LOCK_ID, SyringeNetworking::lockMovement);
    }

    private static CompletableFuture<@Nullable PacketByteBuf> handshake(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
        SyringeMod.isInSyringeServer = true;
        //noinspection OptionalGetWithoutIsPresent
        var metadata = FabricLoader.getInstance().getModContainer(NAMESPACE).get().getMetadata();
        var version = metadata.getVersion().getFriendlyString();
        var protocolStr = metadata.getCustomValue("syringe:protocol").getAsString();
        var protocol = Integer.parseInt(protocolStr);
        var buf1 = PacketByteBufs.create();
        buf1.writeString(version);
        buf1.writeInt(protocol);
        LOGGER.info(String.format("Send handshake packet (version: %s, protocol: %d)", version, protocol));
        return CompletableFuture.completedFuture(buf1);
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
        buf.readList(buf1 -> {
            var id = buf1.readIdentifier();
            var translateKey = buf1.readString();
            var defaultKey = buf1.readEnumConstant(KeyCode.class);
            var keybinding = KeyBinding.KEYS_BY_ID.get(translateKey);
            if (keybinding == null) {
                keybinding = new SyringeKeyBinding(translateKey, defaultKey);
            }
            return new KeybindingEntry(id, keybinding);
        }).forEach(KeyBindingManager::register);
    }

    private static void setPerspective(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var perspective = buf.readEnumConstant(Perspective.class);
        ((ExtendedGameOptions) client.options).setPerspective(perspective, true);
    }

    private static void lockPerspective(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        SyringeMod.isPerspectiveLocked = buf.readBoolean();
    }

    private static void setCameraDirection(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var relative = buf.readBoolean();
        var x = buf.readFloat();
        var y = buf.readFloat();
        var player = client.player;
        if (player != null) {
            var yaw = relative ? player.getYaw() : 0;
            var pitch = relative ? player.getPitch() : 0;
            player.setYaw(yaw + x);
            player.setPitch(pitch + y);
        }
    }

    private static void zoom(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        SyringeMod.zoom = buf.readFloat();
    }

    private static void lockCamera(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        SyringeMod.cameraLock = buf.readBoolean();
    }

    private static void hudHide(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var hudParts = buf.readCollection(Sets::newHashSetWithExpectedSize, b -> b.readEnumConstant(HudPart.class));
        SyringeMod.hidedHudParts.addAll(hudParts);
    }

    private static void hudShow(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var hudParts = buf.readCollection(Sets::newHashSetWithExpectedSize, b -> b.readEnumConstant(HudPart.class));
        SyringeMod.hidedHudParts.removeAll(hudParts);
    }

    private static void lockMovement(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        SyringeMod.movementLock = buf.readBoolean();
    }

    public static void sendKeyPressedPacket(KeybindingEntry entry) {
        var buf = PacketByteBufs.create();
        buf.writeIdentifier(entry.id());
        ClientPlayNetworking.send(KEYBINDING_PRESSED_ID, buf);
    }

    public static void sendKeyReleasedPacket(KeybindingEntry entry) {
        var buf = PacketByteBufs.create();
        buf.writeIdentifier(entry.id());
        ClientPlayNetworking.send(KEYBINDING_RELEASED_ID, buf);
    }
}
