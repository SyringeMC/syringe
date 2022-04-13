package org.syringemc.util;

import net.minecraft.client.option.Perspective;

public interface ExtendedGameOptions {
    void setPerspective(Perspective perspective, boolean force);
}
