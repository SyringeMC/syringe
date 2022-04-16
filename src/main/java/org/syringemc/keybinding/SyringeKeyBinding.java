package org.syringemc.keybinding;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.syringemc.io.SaveDataManager;

public class SyringeKeyBinding extends KeyBinding {
    public SyringeKeyBinding(String translateKey, KeyCode keyCode) {
        super(translateKey, keyCode.getCode(), "Syringe");
    }

    @Override
    public void setBoundKey(InputUtil.Key boundKey) {
        setBoundKey(boundKey, true);
    }

    public void setBoundKey(InputUtil.Key boundKey, boolean save) {
        super.setBoundKey(boundKey);
        if (save) {
            var key = getTranslationKey();
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
