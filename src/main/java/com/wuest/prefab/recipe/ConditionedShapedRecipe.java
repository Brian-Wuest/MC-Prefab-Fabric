package com.wuest.prefab.recipe;

import com.google.common.base.Strings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.*;

public class ConditionedShapedRecipe extends ShapedRecipe {

    final int width;
    final int height;
    final CraftingBookCategory craftingBookCategory;
    final NonNullList<Ingredient> inputs;
    final String group;
    final String configName;
    final boolean recipeHasTags;
    ShapedRecipePattern pattern;
    ItemStack output;
    boolean reloadedTags;
    boolean showNotification;

    public ConditionedShapedRecipe(
            String group,
            CraftingBookCategory craftingBookCategory,
            ShapedRecipePattern pattern,
            ItemStack output,
            String configName,
            boolean recipeHasTags,
            boolean showNotification
    ) {
        super(group, craftingBookCategory, pattern, output, showNotification);

        this.group = group;
        this.craftingBookCategory = craftingBookCategory;
        this.width = pattern.width();
        this.height = pattern.height();
        this.inputs = pattern.ingredients();
        this.output = output;
        this.configName = configName;
        this.recipeHasTags = recipeHasTags;
        this.reloadedTags = false;
        this.showNotification = showNotification;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.ConditionedShapedRecipeSeriaizer;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.craftingBookCategory;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.output;
    }

    @Override
    public  NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public boolean matches(CraftingContainer craftingInventory, Level level) {
        // Make sure to re-load any ingredients associated with tags.
        // This is necessary due to changes in how tags are loaded and how we use configurable recipes.
        if (this.recipeHasTags && !this.reloadedTags) {
            this.validateTagIngredients();
            this.reloadedTags = true;
        }

        for (int i = 0; i <= craftingInventory.getWidth() - this.width; ++i) {
            for (int j = 0; j <= craftingInventory.getHeight() - this.height; ++j) {
                if (this.matches(craftingInventory, i, j, true)) {
                    return true;
                }

                if (this.matches(craftingInventory, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess).copy();
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    private boolean matches(CraftingContainer inv, int offsetX, int offsetY, boolean bl) {
        for (int i = 0; i < inv.getWidth(); ++i) {
            for (int j = 0; j < inv.getHeight(); ++j) {
                int k = i - offsetX;
                int l = j - offsetY;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (bl) {
                        ingredient = this.inputs.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = this.inputs.get(k + l * this.width);
                    }
                }

                if (!ingredient.test(inv.getItem(i + j * inv.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Re-validates the tag ingredients for this recipe.
     * This is necessary because the tags are not loaded at the time when the recipe is initially loaded.
     */
    private void validateTagIngredients() {
        boolean invalidRecipe = false;
        for (Ingredient ingredient : this.getIngredients()) {
            if (ingredient.getItems().length == 0) {
                ingredient.itemStacks = Arrays.stream(ingredient.values).flatMap((value) -> {
                    return value.getItems().stream();
                }).distinct().toArray(ItemStack[]::new);

                if (ingredient.itemStacks.length == 0) {
                    // There are no items associated with this tag; mark this recipe as invalid.
                    invalidRecipe = true;
                    break;
                }
            }
        }

        if (invalidRecipe) {
            this.output = ItemStack.EMPTY;
        } else {
            this.output = ConditionedShapedRecipe.Serializer.validateRecipeOutput(this.output, this.configName);
        }
    }

    public static class Serializer implements RecipeSerializer<ConditionedShapedRecipe> {
        public static final Codec<ConditionedShapedRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(
                    ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter((shapedRecipe) -> {
                return shapedRecipe.group;
            }), CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((shapedRecipe) -> {
                return shapedRecipe.craftingBookCategory;
            }), ShapedRecipePattern.MAP_CODEC.forGetter((shapedRecipe) -> {
                return shapedRecipe.pattern;
            }), ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter((shapedRecipe) -> {
                return shapedRecipe.output;
            }), ExtraCodecs.strictOptionalField(Codec.STRING, "configName", "").forGetter((shapedRecipe) -> {
                        return shapedRecipe.configName;
            }),  ExtraCodecs.strictOptionalField(Codec.BOOL, "recipe_has_tags", true).forGetter((shapedRecipe) -> {
                return shapedRecipe.recipeHasTags;
            }), ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", true).forGetter((shapedRecipe) -> {
                return shapedRecipe.showNotification;
            })).apply(instance, ConditionedShapedRecipe::new);
        });

        public static ItemStack validateRecipeOutput(ItemStack originalOutput, String configName) {
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

        @Override
        public Codec<ConditionedShapedRecipe> codec() {
            return CODEC;
        }

        public ConditionedShapedRecipe fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            String groupName = friendlyByteBuf.readUtf();
            String configName = friendlyByteBuf.readUtf();
            CraftingBookCategory craftingBookCategory = friendlyByteBuf.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedRecipePattern = ShapedRecipePattern.fromNetwork(friendlyByteBuf);

            // Custom bit which validates the recipe output, if the validation fails then an empty itemstack is returned.
            ItemStack itemStack = ConditionedShapedRecipe.Serializer.validateRecipeOutput(friendlyByteBuf.readItem(), configName);

            boolean recipeHasTags = friendlyByteBuf.readBoolean();
            boolean showNotification = friendlyByteBuf.readBoolean();
            return new ConditionedShapedRecipe(groupName, craftingBookCategory, shapedRecipePattern, itemStack, configName, recipeHasTags, showNotification);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ConditionedShapedRecipe shapedRecipe) {
            friendlyByteBuf.writeUtf(shapedRecipe.group);
            friendlyByteBuf.writeUtf(shapedRecipe.configName);
            friendlyByteBuf.writeEnum(shapedRecipe.craftingBookCategory);
            shapedRecipe.pattern.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItem(shapedRecipe.output);
            friendlyByteBuf.writeBoolean(shapedRecipe.recipeHasTags);
            friendlyByteBuf.writeBoolean(shapedRecipe.showNotification);
        }
    }
}
