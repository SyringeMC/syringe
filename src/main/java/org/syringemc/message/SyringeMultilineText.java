package org.syringemc.message;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SyringeMultilineText implements MultilineText {
    private final TextRenderer renderer;
    private final List<Line> lines;
    private final int maxWidth;

    public static final SyringeMultilineText EMPTY = new SyringeMultilineText(null, Collections.emptyList()) {
        public int drawCenterWithShadow(MatrixStack matrices, int x, int y) {
            return y;
        }

        public int drawCenterWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color) {
            return y;
        }

        @Override
        public void drawCenter(MatrixStack matrices, int x, int y, int lineHeight, int color) {
        }

        public int drawWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color) {
            return y;
        }

        public int draw(MatrixStack matrices, int x, int y, int lineHeight, int color) {
            return y;
        }

        public int count() {
            return 0;
        }

        @Override
        public int maxWidth() {
            return 0;
        }
    };

    public SyringeMultilineText(TextRenderer renderer, List<Line> lines) {
        this.renderer = renderer;
        this.lines = lines;
        this.maxWidth = lines.stream().mapToInt(line -> line.width).max().orElse(0);
    }

    @Override
    public int drawCenterWithShadow(MatrixStack matrices, int x, int y) {
        Objects.requireNonNull(renderer);
        return this.drawCenterWithShadow(matrices, x, y, 9, 16777215);
    }

    @Override
    public int drawCenterWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color) {
        int i = y;
        for (Iterator<Line> var7 = lines.iterator(); var7.hasNext(); i += lineHeight) {
            Line line = var7.next();
            renderer.drawWithShadow(matrices, line.text, (float) (x - line.width / 2), (float) i, color);
        }
        return i;
    }

    public void drawCenter(MatrixStack matrices, int x, int y, int lineHeight, int color) {
        int i = y;
        for (Iterator<Line> var7 = lines.iterator(); var7.hasNext(); i += lineHeight) {
            Line line = var7.next();
            renderer.draw(matrices, line.text, (float) (x - line.width / 2), (float) i, color);
        }
    }

    @Override
    public int drawWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color) {
        int i = y;
        for (Iterator<Line> var7 = lines.iterator(); var7.hasNext(); i += lineHeight) {
            Line line = var7.next();
            renderer.drawWithShadow(matrices, line.text, (float) x, (float) i, color);
        }
        return i;
    }

    @Override
    public int draw(MatrixStack matrices, int x, int y, int lineHeight, int color) {
        int i = y;
        for (Iterator<Line> var7 = lines.iterator(); var7.hasNext(); i += lineHeight) {
            Line line = var7.next();
            renderer.draw(matrices, line.text, (float) x, (float) i, color);
        }
        return i;
    }

    @Override
    public void fillBackground(MatrixStack matrices, int centerX, int centerY, int lineHeight, int padding, int color) {
        int i = lines.stream().mapToInt(line -> line.width).max().orElse(0);
        if (i > 0) {
            DrawableHelper.fill(matrices, centerX - i / 2 - padding, centerY - padding, centerX + i / 2 + padding, centerY + lines.size() * lineHeight + padding, color);
        }
    }

    @Override
    public int count() {
        return lines.size();
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }

    public int maxWidth() {
        return lines.stream().mapToInt(line -> line.width).max().orElse(0);
    }

    public static SyringeMultilineText create(TextRenderer renderer, StringVisitable text) {
        return create(renderer, renderer.wrapLines(text, Integer.MAX_VALUE).stream().map((textx) -> new Line(textx, renderer.getWidth(textx))).collect(ImmutableList.toImmutableList()));
    }

    public static SyringeMultilineText create(TextRenderer renderer, List<Line> lines) {
        return lines.isEmpty() ? SyringeMultilineText.EMPTY : new SyringeMultilineText(renderer, lines);
    }
}
