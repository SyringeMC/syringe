package org.syringemc.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public final class TestMod implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(SyringeCommand::register);
        KeybindingTest.startListener();
    }
}
