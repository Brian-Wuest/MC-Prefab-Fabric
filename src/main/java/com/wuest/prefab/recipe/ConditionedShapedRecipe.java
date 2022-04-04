package com.wuest.prefab.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Tuple;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConditionedShapedRecipe extends ShapedRecipe {

    private final int width;
    private final int height;
    private final NonNullList<Ingredient> inputs;
    private final ResourceLocation id;
    private final String group;
    private final String configName;
    private final boolean recipeHasTags;
    private ItemStack output;
    private boolean reloadedTags;

    public ConditionedShapedRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack output, String configName, boolean recipeHasTags) {
        super(id, group, width, height, ingredients, output);

        this.id = id;
        this.group = group;
        this.width = width;
        this.height = height;
        this.inputs = ingredients;
        this.output = output;
        this.configName = configName;
        this.recipeHasTags = recipeHasTags;
        this.reloadedTags = false;
    }

    private static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> key, int width, int height) {
        NonNullList<Ingredient> defaultedList = NonNullList.withSize(width * height, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(key.keySet());
        set.remove(" ");

        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String string = pattern[i].substring(j, j + 1);
                Ingredient ingredient = key.get(string);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }

                set.remove(string);
                defaultedList.set(j + width * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return defaultedList;
        }
    }

    @VisibleForTesting
    static String[] combinePattern(String... lines) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int m = 0; m < lines.length; ++m) {
            String string = lines[m];
            i = Math.min(i, findNextIngredient(string));
            int n = findNextIngredientReverse(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (lines.length == l) {
            return new String[0];
        } else {
            String[] strings = new String[lines.length - l - k];

            for (int o = 0; o < strings.length; ++o) {
                strings[o] = lines[o + k].substring(i, j + 1);
            }

            return strings;
        }
    }

    private static int findNextIngredient(String pattern) {
        int i;
        for (i = 0; i < pattern.length() && pattern.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int findNextIngredientReverse(String pattern) {
        int i;
        for (i = pattern.length() - 1; i >= 0 && pattern.charAt(i) == ' '; --i) {
        }

        return i;
    }

    private static String[] getPattern(JsonArray json) {
        String[] strings = new String[json.size()];
        if (strings.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < strings.length; ++i) {
                String string = GsonHelper.convertToString(json.get(i), "pattern[" + i + "]");
                if (string.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && strings[0].length() != string.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings[i] = string;
            }

            return strings;
        }
    }

    private static Tuple<Boolean, Map<String, Ingredient>> keyFromJson(JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();
        Iterator var2 = json.entrySet().iterator();
        boolean hasTags = false;

        while (var2.hasNext()) {
            Map.Entry<String, JsonElement> entry = (Map.Entry) var2.next();
            if ((entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String) entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            JsonElement ingredientJSON = entry.getValue();
            Ingredient ingredient = Ingredient.fromJson(ingredientJSON);

            ItemStack[] stacks = ingredient.getItems();

            if (ingredient.isEmpty()) {
                // Unable to find a corresponding item for this key. Clear out all entries and return.
                map.clear();
                break;
            }

            if (stacks.length == 0) {
                JsonElement element = ((JsonObject) ingredientJSON).get("tag");

                if (element != null) {
                    hasTags = true;
                }

            }

            map.put(entry.getKey(), ingredient);
        }

        map.put(" ", Ingredient.EMPTY);
        return new Tuple<>(hasTags, map);
    }

    public static ItemStack itemStackFromJson(JsonObject json) {
        String string = GsonHelper.getAsString(json, "item");

        Item item = Registry.ITEM.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + string + "'");
        });

        int stackCount = 1;

        if (GsonHelper.isNumberValue(json, "count")) {
            stackCount = GsonHelper.getAsInt(json, "count");
        }

        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            return new ItemStack(item, stackCount);
        }
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
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
    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputs;
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
    public ItemStack assemble(CraftingContainer craftingInventory) {
        return this.getResultItem().copy();
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

        public ConditionedShapedRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
            String groupName = GsonHelper.getAsString(jsonObject, "group", "");
            String configName = GsonHelper.getAsString(jsonObject, "configName", "");
            Tuple<Boolean, Map<String, Ingredient>> ingredientResult = ConditionedShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(jsonObject, "key"));
            Map<String, Ingredient> map = ingredientResult.getSecond();

            if (map.size() == 1 && map.containsKey(" ")) {
                Prefab.logger.info("Processed EMPTY recipe for location: {}", identifier.toString());

                // This is just an empty recipe. return empty recipe.
                return new ConditionedShapedRecipe(identifier, groupName, 3, 3, NonNullList.withSize(3 * 3, Ingredient.EMPTY), ItemStack.EMPTY, configName, ingredientResult.getFirst());
            }

            String[] strings = ConditionedShapedRecipe.combinePattern(ConditionedShapedRecipe.getPattern(GsonHelper.getAsJsonArray(jsonObject, "pattern")));
            int width = strings[0].length();
            int height = strings.length;
            NonNullList<Ingredient> defaultedList = ConditionedShapedRecipe.dissolvePattern(strings, map, width, height);
            ItemStack itemStack = ConditionedShapedRecipe.Serializer.validateRecipeOutput(ConditionedShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result")), configName);

            return new ConditionedShapedRecipe(identifier, groupName, width, height, defaultedList, itemStack, configName, ingredientResult.getFirst());
        }

        public ConditionedShapedRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
            int width = packetByteBuf.readVarInt();
            int height = packetByteBuf.readVarInt();
            String groupName = packetByteBuf.readUtf(32767);
            String configName = packetByteBuf.readUtf(32767);
            boolean recipeHasTags = packetByteBuf.readBoolean();

            NonNullList<Ingredient> defaultedList = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int k = 0; k < defaultedList.size(); ++k) {
                defaultedList.set(k, Ingredient.fromNetwork(packetByteBuf));
            }

            ItemStack itemStack = this.validateRecipeOutput(packetByteBuf.readItem(), configName);
            return new ConditionedShapedRecipe(identifier, groupName, width, height, defaultedList, itemStack, configName, recipeHasTags);
        }

        public void toNetwork(FriendlyByteBuf packetByteBuf, ConditionedShapedRecipe shapedRecipe) {
            packetByteBuf.writeVarInt(shapedRecipe.width);
            packetByteBuf.writeVarInt(shapedRecipe.height);
            packetByteBuf.writeUtf(shapedRecipe.group);
            packetByteBuf.writeUtf(shapedRecipe.configName);
            packetByteBuf.writeBoolean(shapedRecipe.recipeHasTags);

            Iterator var3 = shapedRecipe.inputs.iterator();

            while (var3.hasNext()) {
                Ingredient ingredient = (Ingredient) var3.next();
                ingredient.toNetwork(packetByteBuf);
            }

            packetByteBuf.writeItem(shapedRecipe.output);
        }
    }
}
