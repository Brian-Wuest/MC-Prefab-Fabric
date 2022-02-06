package com.wuest.prefab.config;

import com.wuest.prefab.Utils;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to create GUI mappings for the recipe listings.
 */
public class RecipeMapGuiProvider implements GuiProvider {

    @Override
    public List<AbstractConfigListEntry> get(String s, Field field, Object savedObject, Object defaultObject, GuiRegistryAccess guiRegistryAccess) {
        try {
            HashMap<String, Boolean> savedHashMap = (HashMap<String, Boolean>) field.get(savedObject);
            HashMap<String, Boolean> defaultHashMap = (HashMap<String, Boolean>) field.get(defaultObject);

            if (savedHashMap.size() != defaultHashMap.size()) {
                for (Map.Entry<String, Boolean> defaultEntry : defaultHashMap.entrySet()) {
                    // Make sure that the saved hashmap has this entry; if not add it by default.
                    if (!savedHashMap.containsKey(defaultEntry.getKey())) {
                        savedHashMap.put(defaultEntry.getKey(), defaultEntry.getValue());
                    }
                }
            }

            ArrayList<AbstractConfigListEntry> entries = new ArrayList<>();
            for (Map.Entry<String, Boolean> map : savedHashMap.entrySet()) {
                BooleanListEntry entry = new BooleanListEntry(Utils.createTextComponent(map.getKey()), map.getValue(),
                        Utils.createTextComponent("Reset"), () -> true, (value) -> {
                    map.setValue(value);
                }, () -> java.util.Optional.of(new Component[]{Utils.createTextComponent("Enables or disables recipes for " + map.getKey())}));

                entries.add(entry);
            }

            return entries;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}