package org.syringemc.keybinding;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.syringemc.io.SaveDataManager;

public class SyringeKeyBinding extends KeyBinding {
    public final Identifier id;

    public SyringeKeyBinding(Identifier id, KeyCode keyCode) {
        super(String.format("key.%s.%s", id.getNamespace(), id.getPath()), keyCode.getCode(), "Syringe");
        this.id = id;
    }

    @Override
    public void setBoundKey(InputUtil.Key boundKey) {
        setBoundKey(boundKey, true);
    }

    public void setBoundKey(InputUtil.Key boundKey, boolean save) {
        super.setBoundKey(boundKey);
        if (save) {
            var key = id.toString();
            if (boundKey == getDefaultKey()) {
                SaveDataManager.remove(key);
            } else {
                SaveDataManager.setInt(key, boundKey.getCode());
            }
            SaveDataManager.save();
            KeyBinding.updateKeysByCode();
        }
    }
}
