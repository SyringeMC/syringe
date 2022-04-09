package org.syringemc.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.LiteralText;
import org.syringemc.message.MessagePosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class MessagePositionArgumentType implements ArgumentType<MessagePosition> {

    private static final Collection<String> EXAMPLES = new ArrayList<>() {{
        add(MessagePosition.TOP_LEFT.name());
        add(MessagePosition.MIDDLE_CENTER.name());
        add(MessagePosition.BOTTOM_RIGHT.name());
    }};

    public static MessagePositionArgumentType messagePosition() {
        return new MessagePositionArgumentType();
    }

    public static <S> MessagePosition getMessagePosition(CommandContext<S> context, String name) {
        return context.getArgument(name, MessagePosition.class);
    }

    @Override
    public MessagePosition parse(StringReader reader) throws CommandSyntaxException {
        var argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        var string = reader.getString().substring(argBeginning, reader.getCursor());
        try {
            return MessagePosition.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(new LiteralText(e.getMessage())).createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (MessagePosition position : MessagePosition.values()) {
            var name = position.name().toLowerCase();
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
