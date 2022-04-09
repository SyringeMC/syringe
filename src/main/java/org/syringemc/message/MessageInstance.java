package org.syringemc.message;

import org.jetbrains.annotations.Nullable;

public class MessageInstance {
    private final MessageContext context;
    private final long startTime;
    private final long fadeInTime;
    private @Nullable Long endTime;
    private @Nullable Long fadeOutTime;

    public MessageInstance(MessageContext context, long startTime, long fadeInTime) {
        this.context = context;
        this.startTime = startTime;
        this.fadeInTime = fadeInTime;
    }

    public MessageInstance(MessageContext context, long startTime, long fadeInTime, @Nullable Long endTime, @Nullable Long fadeOutTime) {
        this.context = context;
        this.startTime = startTime;
        this.fadeInTime = fadeInTime;
        this.endTime = endTime;
        this.fadeOutTime = fadeOutTime;
    }

    public MessageContext getContext() {
        return context;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFadeInTime() {
        return fadeInTime;
    }

    public @Nullable Long getEndTime() {
        return endTime;
    }

    public void setEndTime(@Nullable Long endTime) {
        this.endTime = endTime;
    }

    public @Nullable Long getFadeOutTime() {
        return fadeOutTime;
    }

    public void setFadeOutTime(@Nullable Long fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
    }
}
