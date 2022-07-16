package dev.uten2c.syringe.message;

import net.minecraft.text.Text;

public record MessageContext(Text message, boolean shadow, float size, int lineHeight, MessagePosition position, int offsetX, int offsetY) {
}
