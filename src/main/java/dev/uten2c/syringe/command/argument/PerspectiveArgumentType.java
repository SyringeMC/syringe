package dev.uten2c.syringe.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.option.Perspective;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PerspectiveArgumentType implements ArgumentType<Perspective> {
    private static final Collection<String> EXAMPLES = new ArrayList<>() {{
        add(Perspective.FIRST_PERSON.name().toLowerCase());
        add(Perspective.THIRD_PERSON_BACK.name().toLowerCase());
        add(Perspective.THIRD_PERSON_FRONT.name().toLowerCase());
    }};

    public static PerspectiveArgumentType perspective() {
        return new PerspectiveArgumentType();
    }

    public static <S> Perspective getPerspective(CommandContext<S> context, String name) {
        return context.getArgument(name, Perspective.class);
    }

    @Override
    public Perspective parse(StringReader reader) throws CommandSyntaxException {
        var argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        var string = reader.getString().substring(argBeginning, reader.getCursor());
        try {
            return Perspective.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(Text.of(e.getMessage())).createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (var part : Perspective.values()) {
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
