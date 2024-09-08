package com.wuest.prefab.recipe;

import com.google.common.base.Strings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

public class ConditionedShapelessRecipe extends ShapelessRecipe {
    private final String group;
    CraftingBookCategory category;
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;
    private final String configName;

    public ConditionedShapelessRecipe(
            String group,
            CraftingBookCategory craftingBookCategory,
            ItemStack output,
            NonNullList<Ingredient> ingredients,
            String configName
    ) {
        super(group, craftingBookCategory, output, ingredients);

        this.group = group;
        this.output = output;
        this.ingredients = ingredients;
        this.configName = configName;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.ConditionedShapelessRecipeSeriaizer;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matches(CraftingContainer craftingInventory, Level world) {
        StackedContents stackedContents = new StackedContents();
        int i = 0;

        for (int j = 0; j < craftingInventory.getContainerSize(); ++j) {
            ItemStack itemStack = craftingInventory.getItem(j);

            if (!itemStack.isEmpty()) {
                ++i;
                stackedContents.accountStack(itemStack, 1);
            }
        }

        return i == this.ingredients.size() && stackedContents.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        return this.output.copy();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    public static class Serializer implements RecipeSerializer<ConditionedShapelessRecipe> {
        private static final Codec<ConditionedShapelessRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(
                    ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter((shapelessRecipe) -> {
                        return shapelessRecipe.group;
                    }), CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((shapelessRecipe) -> {
                        return shapelessRecipe.category;
                    }), ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter((shapelessRecipe) -> {
                        return shapelessRecipe.output;
                    }), Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((list) -> {
                        Ingredient[] ingredients = list.stream().filter((ingredient) -> {
                            return !ingredient.isEmpty();
                        }).toArray(Ingredient[]::new);
                        if (ingredients.length == 0) {
                            return DataResult.error(() -> {
                                return "No ingredients for shapeless recipe";
                            });
                        } else {
                            return ingredients.length > 9 ? DataResult.error(() -> {
                                return "Too many ingredients for shapeless recipe";
                            }) : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }
                    }, DataResult::success).forGetter((shapelessRecipe) -> {
                        return shapelessRecipe.ingredients;
                    }), ExtraCodecs.strictOptionalField(Codec.STRING, "config", "").forGetter((shapelessRecipe) -> {
                        return shapelessRecipe.group;
                    })).apply(instance, ConditionedShapelessRecipe::new);
        });

        @Override
        public Codec<ConditionedShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public ConditionedShapelessRecipe fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            String groupName = friendlyByteBuf.readUtf();
            String configName = friendlyByteBuf.readUtf();
            CraftingBookCategory craftingBookCategory = friendlyByteBuf.readEnum(CraftingBookCategory.class);
            int i = friendlyByteBuf.readVarInt();

            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);

            nonNullList.replaceAll(ignored -> Ingredient.fromNetwork(friendlyByteBuf));

            ItemStack itemStack = this.validateRecipeOutput(friendlyByteBuf.readItem(), configName);

            return new ConditionedShapelessRecipe(groupName, craftingBookCategory, itemStack, nonNullList, configName);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ConditionedShapelessRecipe shapelessRecipe) {
            friendlyByteBuf.writeUtf(shapelessRecipe.group);
            friendlyByteBuf.writeUtf(shapelessRecipe.configName);
            friendlyByteBuf.writeEnum(shapelessRecipe.category);
            friendlyByteBuf.writeVarInt(shapelessRecipe.ingredients.size());

            for (Ingredient ingredient : shapelessRecipe.ingredients) {
                ingredient.toNetwork(friendlyByteBuf);
            }

            friendlyByteBuf.writeItem(shapelessRecipe.output);
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
