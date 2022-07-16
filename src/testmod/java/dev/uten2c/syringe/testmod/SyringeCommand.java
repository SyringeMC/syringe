package dev.uten2c.syringe.testmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.uten2c.syringe.command.argument.MessagePositionArgumentType;
import dev.uten2c.syringe.network.SyringeNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class SyringeCommand {
    private SyringeCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("syringe").then(message()).then(perspective()));
    }

    // syringe message display <targets> <id> <message> <shadow> <size> <line height> <position> <offset x> <offset y> <fadein>
    // syringe message discard <targets> <id> <fadeout>
    // syringe message clear <targets>
    private static LiteralArgumentBuilder<ServerCommandSource> message() {
        var message = literal("message");
        var display = literal("display").then(argument("targets", EntityArgumentType.players()).then(argument("id", StringArgumentType.string()).then(argument("shadow", BoolArgumentType.bool()).then(argument("size", FloatArgumentType.floatArg(0)).then(argument("line height", IntegerArgumentType.integer()).then(argument("position", MessagePositionArgumentType.messagePosition()).then(argument("offset x", IntegerArgumentType.integer()).then(argument("offset y", IntegerArgumentType.integer()).then(argument("fadein", LongArgumentType.longArg()).then(argument("message", TextArgumentType.text()).executes(ctx -> {
            var buf = PacketByteBufs.create();
            buf.writeString(StringArgumentType.getString(ctx, "id"));
            buf.writeText(TextArgumentType.getTextArgument(ctx, "message"));
            buf.writeBoolean(BoolArgumentType.getBool(ctx, "shadow"));
            buf.writeFloat(FloatArgumentType.getFloat(ctx, "size"));
            buf.writeInt(IntegerArgumentType.getInteger(ctx, "line height"));
            buf.writeEnumConstant(MessagePositionArgumentType.getMessagePosition(ctx, "position"));
            buf.writeInt(IntegerArgumentType.getInteger(ctx, "offset x"));
            buf.writeInt(IntegerArgumentType.getInteger(ctx, "offset y"));
            buf.writeLong(LongArgumentType.getLong(ctx, "fadein"));
            EntityArgumentType.getPlayers(ctx, "targets").forEach(player -> ServerPlayNetworking.send(player, SyringeNetworking.MESSAGE_DISPLAY_ID, buf));
            return 1;
        })))))))))));
        var discard = literal("discard").then(argument("targets", EntityArgumentType.players()).then(argument("id", StringArgumentType.string()).then(argument("fadeout", LongArgumentType.longArg(0)).executes(ctx -> {
            var buf = PacketByteBufs.create();
            buf.writeString(StringArgumentType.getString(ctx, "id"));
            buf.writeLong(LongArgumentType.getLong(ctx, "fadeout"));
            EntityArgumentType.getPlayers(ctx, "targets").forEach(player -> ServerPlayNetworking.send(player, SyringeNetworking.MESSAGE_DISCARD_ID, buf));
            return 1;
        }))));
        var clear = literal("clear").then(argument("targets", EntityArgumentType.players()).executes(ctx -> {
            EntityArgumentType.getPlayers(ctx, "targets").forEach(player -> ServerPlayNetworking.send(player, SyringeNetworking.MESSAGE_CLEAR_ID, PacketByteBufs.empty()));
            return 1;
        }));
        message.then(display);
        message.then(discard);
        message.then(clear);
        return message;
    }

    // syringe perspective set <targets> <id>
    // syringe perspective lock <targets> <boolean>
    private static LiteralArgumentBuilder<ServerCommandSource> perspective() {
        var perspective = literal("perspective");
        var set = literal("set").then(argument("targets", EntityArgumentType.players()).then(argument("id", IntegerArgumentType.integer(0, 2)).executes(ctx -> {
            var id = IntegerArgumentType.getInteger(ctx, "id");
            var buf = PacketByteBufs.create();
            buf.writeInt(id);
            EntityArgumentType.getPlayers(ctx, "targets").forEach(player -> ServerPlayNetworking.send(player, SyringeNetworking.PERSPECTIVE_SET_ID, buf));
            return id;
        })));
        var lock = literal("lock").then(argument("targets", EntityArgumentType.players()).then(argument("lock", BoolArgumentType.bool()).executes(ctx -> {
            var buf = PacketByteBufs.create();
            buf.writeBoolean(BoolArgumentType.getBool(ctx, "lock"));
            EntityArgumentType.getPlayers(ctx, "targets").forEach(player -> ServerPlayNetworking.send(player, SyringeNetworking.PERSPECTIVE_LOCK_ID, buf));
            return 1;
        })));
        perspective.then(set);
        perspective.then(lock);
        return perspective;
    }
}
