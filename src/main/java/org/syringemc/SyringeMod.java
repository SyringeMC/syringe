package org.syringemc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import org.syringemc.command.argument.MessagePositionArgumentType;
import org.syringemc.io.SaveDataManager;
import org.syringemc.keybinding.KeyBindingManager;
import org.syringemc.network.SyringeNetworking;

public final class SyringeMod implements ModInitializer {
    @Override
    public void onInitialize() {
        SaveDataManager.load();
        SyringeNetworking.registerReceivers();
        KeyBindingManager.setup();

        ArgumentTypes.register("syringe:position", MessagePositionArgumentType.class, new ConstantArgumentSerializer<>(MessagePositionArgumentType::messagePosition));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onDisconnected());
    }

    private static void onDisconnected() {
        SyringeNetworking.MESSAGES.clear();
        KeyBindingManager.reset();
    }
}
