package dev.uten2c.syringe.keybinding;

import dev.uten2c.syringe.io.SaveDataManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class SyringeKeyBinding extends KeyBinding {
    public static final String CATEGORY = "__syringe__";

    public SyringeKeyBinding(String translateKey, KeyCode keyCode) {
        super(translateKey, keyCode.getCode(), CATEGORY);
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
