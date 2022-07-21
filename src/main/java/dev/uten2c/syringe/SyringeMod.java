package dev.uten2c.syringe;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.uten2c.syringe.command.argument.HudPartArgumentType;
import dev.uten2c.syringe.command.argument.MessagePositionArgumentType;
import dev.uten2c.syringe.command.argument.PerspectiveArgumentType;
import dev.uten2c.syringe.hud.HudPart;
import dev.uten2c.syringe.io.SaveDataManager;
import dev.uten2c.syringe.keybinding.KeyBindingManager;
import dev.uten2c.syringe.network.SyringeNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public final class SyringeMod implements ModInitializer {
    public static boolean isPerspectiveLocked = false;
    public static Set<HudPart> hidedHudParts = new HashSet<>();
    public static float zoom = 1f;
    public static boolean isInSyringeServer = false;

    @Override
    public void onInitialize() {
        SaveDataManager.load();
        SyringeNetworking.registerReceivers();
        KeyBindingManager.setup();

        registerArgumentType(new Identifier("syringe", "position"), MessagePositionArgumentType.class, MessagePositionArgumentType::messagePosition);
        registerArgumentType(new Identifier("syringe", "perspective"), PerspectiveArgumentType.class, PerspectiveArgumentType::perspective);
        registerArgumentType(new Identifier("syringe", "hud_part"), HudPartArgumentType.class, HudPartArgumentType::hudPart);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, server) -> onDisconnected());
    }

    private static void onDisconnected() {
        SyringeNetworking.MESSAGES.clear();
        KeyBindingManager.reset();
        isPerspectiveLocked = false;
        hidedHudParts.clear();
        zoom = 1f;
        isInSyringeServer = false;
    }

    private static <T extends ArgumentType<?>> void registerArgumentType(Identifier id, Class<T> clazz, Supplier<T> typeSupplier) {
        ArgumentTypeRegistry.registerArgumentType(id, clazz, ConstantArgumentSerializer.of(typeSupplier));
    }
}
