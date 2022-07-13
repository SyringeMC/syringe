package org.syringemc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import org.syringemc.command.argument.MessagePositionArgumentType;
import org.syringemc.io.SaveDataManager;
import org.syringemc.keybinding.KeyBindingManager;
import org.syringemc.network.SyringeNetworking;

public final class SyringeMod implements ModInitializer {
    public static boolean isPerspectiveLocked = false;

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

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onDisconnected());
    }

    private static void onDisconnected() {
        SyringeNetworking.MESSAGES.clear();
        KeyBindingManager.reset();
        isPerspectiveLocked = false;
    }
}
