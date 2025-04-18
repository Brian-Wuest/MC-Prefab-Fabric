package com.wuest.prefab.recipe;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

import java.util.Iterator;

public class ConditionedShapelessRecipe extends ShapelessRecipe {
    private final ResourceLocation id;
    private final String group;
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;
    private final String configName;
    private final CraftingBookCategory craftingBookCategory;

    public ConditionedShapelessRecipe(
            ResourceLocation id,
            String group,
            CraftingBookCategory craftingBookCategory,
            ItemStack output,
            NonNullList<Ingredient> ingredients,
            String configName) {
        super(id, group, craftingBookCategory, output, ingredients);

        this.id = id;
        this.group = group;
        this.output = output;
        this.ingredients = ingredients;
        this.configName = configName;
        this.craftingBookCategory = craftingBookCategory;
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
        public ConditionedShapelessRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
            String groupName = GsonHelper.getAsString(jsonObject, "group", "");
            String configName = GsonHelper.getAsString(jsonObject, "configName", "");
            NonNullList<Ingredient> defaultedList = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));

            if (defaultedList.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (defaultedList.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            } else {
                ItemStack itemStack = this.validateRecipeOutput(ConditionedShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result")), configName);
                return new ConditionedShapelessRecipe(identifier, groupName, CraftingBookCategory.MISC, itemStack, defaultedList, configName);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray json) {
            NonNullList<Ingredient> defaultedList = NonNullList.create();

            for (int i = 0; i < json.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i));
                if (!ingredient.isEmpty()) {
                    defaultedList.add(ingredient);
                }
            }

            return defaultedList;
        }

        @Override
        public ConditionedShapelessRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf friendlyByteBuf) {
            String groupName = friendlyByteBuf.readUtf(32767);
            String configName = friendlyByteBuf.readUtf(32767);
            int i = friendlyByteBuf.readVarInt();
            NonNullList<Ingredient> defaultedList = NonNullList.withSize(i, Ingredient.EMPTY);

            defaultedList.replaceAll(ignored -> Ingredient.fromNetwork(friendlyByteBuf));

            ItemStack itemStack = this.validateRecipeOutput(friendlyByteBuf.readItem(), configName);
            return new ConditionedShapelessRecipe(
                    identifier,
                    groupName,
                    CraftingBookCategory.MISC,
                    itemStack,
                    defaultedList,
                    configName);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ConditionedShapelessRecipe shapelessRecipe) {
            friendlyByteBuf.writeUtf(shapelessRecipe.group);
            friendlyByteBuf.writeUtf(shapelessRecipe.configName);
            friendlyByteBuf.writeVarInt(shapelessRecipe.ingredients.size());
            Iterator<Ingredient> var3 = shapelessRecipe.ingredients.iterator();

            while (var3.hasNext()) {
                Ingredient ingredient = var3.next();
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
