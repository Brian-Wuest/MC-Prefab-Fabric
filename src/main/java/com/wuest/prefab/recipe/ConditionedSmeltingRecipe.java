package com.wuest.prefab.recipe;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wuest.prefab.Prefab;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class ConditionedSmeltingRecipe extends SmeltingRecipe {
    private final String configName;

    public ConditionedSmeltingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, String configName) {
        super(id, group, input, output, experience, cookTime);

        this.configName = configName;
    }

    public ItemStack createIcon() {
        return new ItemStack(Blocks.FURNACE);
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMELTING;
    }

    public static class Serializer implements RecipeSerializer<ConditionedSmeltingRecipe> {
        public ConditionedSmeltingRecipe read(Identifier identifier, JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "group", "");
            String configName = JsonHelper.getString(jsonObject, "configName", "");
            JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
            Ingredient ingredient = Ingredient.fromJson((JsonElement)jsonElement);
            String string2 = JsonHelper.getString(jsonObject, "result");
            Identifier identifier2 = new Identifier(string2);
            ItemStack itemStack = new ItemStack((ItemConvertible) Registry.ITEM.getOrEmpty(identifier2).orElseThrow(() -> {
                return new IllegalStateException("Item: " + string2 + " does not exist");
            }));

            itemStack = this.validateRecipeOutput(itemStack, configName);

            float experience = JsonHelper.getFloat(jsonObject, "experience", 0.0F);
            int cookingtime = JsonHelper.getInt(jsonObject, "cookingtime", 200);
            return new ConditionedSmeltingRecipe(identifier, string, ingredient, itemStack, experience, cookingtime, configName);
        }

        public ConditionedSmeltingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            String group = packetByteBuf.readString();
            String configName = packetByteBuf.readString() ;
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = this.validateRecipeOutput(packetByteBuf.readItemStack(), configName);
            float experience = packetByteBuf.readFloat();
            int cookTime = packetByteBuf.readVarInt();
            return new ConditionedSmeltingRecipe(identifier, group, ingredient, itemStack, experience, cookTime, configName);
        }

        public void write(PacketByteBuf packetByteBuf, ConditionedSmeltingRecipe abstractCookingRecipe) {
            packetByteBuf.writeString(abstractCookingRecipe.group);
            packetByteBuf.writeString(abstractCookingRecipe.configName);
            abstractCookingRecipe.input.write(packetByteBuf);
            packetByteBuf.writeItemStack(abstractCookingRecipe.output);
            packetByteBuf.writeFloat(abstractCookingRecipe.experience);
            packetByteBuf.writeVarInt(abstractCookingRecipe.cookTime);
        }

        public ItemStack validateRecipeOutput(ItemStack originalOutput, String configName) {
            if (originalOutput == ItemStack.EMPTY) {
                return ItemStack.EMPTY;
            }

            if (!Strings.isNullOrEmpty(configName)
                    && Prefab.serverConfiguration.recipes.containsKey(configName)
                    && !Prefab.serverConfiguration.recipes.get(configName)) {
                // The configuration option for this recipe was turned off.
                // Specify that the recipe has no output which basically makes it disabled.
                return ItemStack.EMPTY;
            }

            return originalOutput;
        }
    }

}
