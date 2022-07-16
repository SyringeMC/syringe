package dev.uten2c.syringe.testmod;

import dev.uten2c.syringe.network.SyringeNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HandshakeTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandshakeTest.class);

    private HandshakeTest() {
    }

    public static void setup() {
        ServerLoginConnectionEvents.QUERY_START.register(HandshakeTest::onLoginStart);
        ServerLoginNetworking.registerGlobalReceiver(SyringeNetworking.HANDSHAKE_ID, (server, handler, understood, buf, synchronizer, sender) -> {
            if (understood) {
                var version = buf.readString();
                var protocol = buf.readInt();
                LOGGER.info(String.format("Receive handshake packet (version: %s, protocol: %d)", version, protocol));
            }
        });
    }

    private static void onLoginStart(ServerLoginNetworkHandler networkHandler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
        sender.sendPacket(SyringeNetworking.HANDSHAKE_ID, PacketByteBufs.empty());
    }
}
