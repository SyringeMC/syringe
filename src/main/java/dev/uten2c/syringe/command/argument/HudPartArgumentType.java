package dev.uten2c.syringe.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.uten2c.syringe.hud.HudPart;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class HudPartArgumentType implements ArgumentType<HudPart> {

    private static final Collection<String> EXAMPLES = new ArrayList<>() {{
        add(HudPart.CROSSHAIR.name());
        add(HudPart.HOTBAR.name());
        add(HudPart.BOSS_BAR.name());
    }};

    public static HudPartArgumentType hudPart() {
        return new HudPartArgumentType();
    }

    public static <S> HudPart getHudPart(CommandContext<S> context, String name) {
        return context.getArgument(name, HudPart.class);
    }

    @Override
    public HudPart parse(StringReader reader) throws CommandSyntaxException {
        var argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        var string = reader.getString().substring(argBeginning, reader.getCursor());
        try {
            return HudPart.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(Text.of(e.getMessage())).createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (var part : HudPart.values()) {
            var name = part.name().toLowerCase();
            if (name.startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
