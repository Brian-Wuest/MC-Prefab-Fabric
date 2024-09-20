package com.wuest.prefab.recipe;

import com.google.common.base.Strings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wuest.prefab.Prefab;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

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

    public static class Serializer implements RecipeSerializer<ConditionedSmeltingRecipe> {
        public static final Codec<ConditionedSmeltingRecipe> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(
                        ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter((o) -> o.group),
                        CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter((o) -> o.category),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((o) -> o.ingredient),
                        ItemStack.CODEC.fieldOf("result").forGetter((o) -> o.result),
                        ExtraCodecs.strictOptionalField(Codec.FLOAT, "experience", 0.1f).forGetter((o) -> o.experience),
                        ExtraCodecs.strictOptionalField(Codec.INT, "cookingtime", 200).forGetter((o) -> o.cookingTime),
                        ExtraCodecs.strictOptionalField(Codec.STRING, "configName", "").forGetter((o) -> o.configName)

                ).apply(instance, ConditionedSmeltingRecipe::new)
        );

        @Override
        public Codec<ConditionedSmeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public ConditionedSmeltingRecipe fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            String group = friendlyByteBuf.readUtf();
            String configName = friendlyByteBuf.readUtf();
            CookingBookCategory cookingBookCategory = friendlyByteBuf.readEnum(CookingBookCategory.class);
            Ingredient ingredient = Ingredient.fromNetwork(friendlyByteBuf);
            ItemStack itemStack = this.validateRecipeOutput(friendlyByteBuf.readItem(), configName);
            float experience = friendlyByteBuf.readFloat();
            int cookTime = friendlyByteBuf.readVarInt();
            return new ConditionedSmeltingRecipe(group, cookingBookCategory, ingredient, itemStack, experience, cookTime, configName);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ConditionedSmeltingRecipe abstractCookingRecipe) {
            friendlyByteBuf.writeUtf(abstractCookingRecipe.group);
            friendlyByteBuf.writeUtf(abstractCookingRecipe.configName);
            friendlyByteBuf.writeEnum(abstractCookingRecipe.category());
            abstractCookingRecipe.ingredient.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItem(abstractCookingRecipe.result);
            friendlyByteBuf.writeFloat(abstractCookingRecipe.experience);
            friendlyByteBuf.writeVarInt(abstractCookingRecipe.cookingTime);
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
