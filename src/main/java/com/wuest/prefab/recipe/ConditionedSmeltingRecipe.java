package com.wuest.prefab.recipe;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wuest.prefab.Prefab;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Blocks;

public class ConditionedSmeltingRecipe extends SmeltingRecipe {
    private final String configName;

    public ConditionedSmeltingRecipe(
            String group,
            CookingBookCategory cookingBookCategory,
            Ingredient input,
            ItemStack output,
            float experience,
            int cookTime,
            String configName
    ) {
        super(group, cookingBookCategory, input, output, experience, cookTime);

        this.configName = configName;
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.FURNACE);
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMELTING_RECIPE;
    }

    public static class Serializer implements RecipeSerializer<ConditionedSmeltingRecipe> {
        //        public ConditionedSmeltingRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
//            String string = GsonHelper.getAsString(jsonObject, "group", "");
//            String configName = GsonHelper.getAsString(jsonObject, "configName", "");
//            JsonElement jsonElement = GsonHelper.isArrayNode(jsonObject, "ingredient") ?  GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient");
//            Ingredient ingredient = Ingredient.fromJson((JsonElement)jsonElement);
//            String string2 = GsonHelper.getAsString(jsonObject, "result");
//            ResourceLocation identifier2 = new ResourceLocation(string2);
//            ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.getOptional(identifier2).orElseThrow(() -> {
//                return new IllegalStateException("Item: " + string2 + " does not exist");
//            }));
//
//            itemStack = this.validateRecipeOutput(itemStack, configName);
//
//            float experience = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
//            int cookingtime = GsonHelper.getAsInt(jsonObject, "cookingtime", 200);
//            return new ConditionedSmeltingRecipe(identifier, string, CookingBookCategory.MISC, ingredient, itemStack, experience, cookingtime, configName);
//        }
        public static final Codec<ConditionedSmeltingRecipe> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Codec.STRING.fieldOf("group").forGetter((o) -> o.group),
                        CookingBookCategory.CODEC.fieldOf("cooking_book_category").forGetter((o) -> o.category),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((o) -> o.ingredient),
                        ItemStack.CODEC.fieldOf("output").forGetter((o) -> o.result),
                        Codec.FLOAT.fieldOf("experience").forGetter((o) -> o.experience),
                        Codec.INT.fieldOf("cookingtime").forGetter((o) -> o.cookingTime),
                        Codec.STRING.fieldOf("config_name").forGetter((o) -> o.configName)

                        ).apply(instance, ConditionedSmeltingRecipe::new)
        );

        @Override
        public Codec<ConditionedSmeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public ConditionedSmeltingRecipe fromNetwork(FriendlyByteBuf packetByteBuf) {
            String group = packetByteBuf.readUtf();
            String configName = packetByteBuf.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(packetByteBuf);
            ItemStack itemStack = this.validateRecipeOutput(packetByteBuf.readItem(), configName);
            float experience = packetByteBuf.readFloat();
            int cookTime = packetByteBuf.readVarInt();
            return new ConditionedSmeltingRecipe(group, CookingBookCategory.MISC, ingredient, itemStack, experience, cookTime, configName);
        }

        @Override
        public void toNetwork(FriendlyByteBuf packetByteBuf, ConditionedSmeltingRecipe abstractCookingRecipe) {
            packetByteBuf.writeUtf(abstractCookingRecipe.group);
            packetByteBuf.writeUtf(abstractCookingRecipe.configName);
            abstractCookingRecipe.ingredient.toNetwork(packetByteBuf);
            packetByteBuf.writeItem(abstractCookingRecipe.result);
            packetByteBuf.writeFloat(abstractCookingRecipe.experience);
            packetByteBuf.writeVarInt(abstractCookingRecipe.cookingTime);
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
