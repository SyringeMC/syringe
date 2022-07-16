package dev.uten2c.syringe;

import dev.uten2c.syringe.command.argument.HudPartArgumentType;
import dev.uten2c.syringe.command.argument.MessagePositionArgumentType;
import dev.uten2c.syringe.hud.HudPart;
import dev.uten2c.syringe.io.SaveDataManager;
import dev.uten2c.syringe.keybinding.KeyBindingManager;
import dev.uten2c.syringe.network.SyringeNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public final class SyringeMod implements ModInitializer {
    public static boolean isPerspectiveLocked = false;
    public static Set<HudPart> hidedHudParts = new HashSet<>();
    public static double zoom = 1.0;
    public static boolean isInSyringeServer = false;

    @Override
    public void onInitialize() {
        SaveDataManager.load();
        SyringeNetworking.registerReceivers();
        KeyBindingManager.setup();

        ArgumentTypeRegistry.registerArgumentType(
            new Identifier("syringe", "position"),
            MessagePositionArgumentType.class,
            ConstantArgumentSerializer.of(MessagePositionArgumentType::messagePosition)
        );
        ArgumentTypeRegistry.registerArgumentType(
            new Identifier("syringe", "hud_part"),
            HudPartArgumentType.class,
            ConstantArgumentSerializer.of(HudPartArgumentType::hudPart)
        );

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onDisconnected());
    }

    private static void onDisconnected() {
        SyringeNetworking.MESSAGES.clear();
        KeyBindingManager.reset();
        isPerspectiveLocked = false;
        hidedHudParts.clear();
        zoom = 1.0;
        isInSyringeServer = false;
    }
}
