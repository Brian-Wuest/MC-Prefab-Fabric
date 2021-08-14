package com.wuest.prefab.recipe;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Iterator;

public class ConditionedShaplessRecipe extends ShapelessRecipe {
    private final Identifier id;
    private final String group;
    private final ItemStack output;
    private final DefaultedList<Ingredient> input;
    private final String configName;

    public ConditionedShaplessRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input, String configName) {
        super(id, group, output, input);

        this.id = id;
        this.group = group;
        this.output = output;
        this.input = input;
        this.configName = configName;
    }

    public Identifier getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.ConditionedShapelessRecipeSeriaizer;
    }

    @Environment(EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public DefaultedList<Ingredient> getPreviewInputs() {
        return this.input;
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        RecipeMatcher recipeFinder = new RecipeMatcher();
        int i = 0;

        for (int j = 0; j < craftingInventory.size(); ++j) {
            ItemStack itemStack = craftingInventory.getStack(j);
            if (!itemStack.isEmpty()) {
                ++i;
                recipeFinder.method_20478(itemStack, 1);
            }
        }

        return i == this.input.size() && recipeFinder.match(this, (IntList) null);
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        return this.output.copy();
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= this.input.size();
    }

    public static class Serializer implements RecipeSerializer<ConditionedShaplessRecipe> {
        private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
            DefaultedList<Ingredient> defaultedList = DefaultedList.of();

            for (int i = 0; i < json.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i));
                if (!ingredient.isEmpty()) {
                    defaultedList.add(ingredient);
                }
            }

            return defaultedList;
        }

        public ConditionedShaplessRecipe read(Identifier identifier, JsonObject jsonObject) {
            String groupName = JsonHelper.getString(jsonObject, "group", "");
            String configName = JsonHelper.getString(jsonObject, "configName", "");
            DefaultedList<Ingredient> defaultedList = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));

            if (defaultedList.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (defaultedList.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            } else {
                ItemStack itemStack = this.validateRecipeOutput(ConditionedShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result")), configName);
                return new ConditionedShaplessRecipe(identifier, groupName, itemStack, defaultedList, configName);
            }
        }

        public ConditionedShaplessRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            String groupName = packetByteBuf.readString(32767);
            String configName = packetByteBuf.readString(32767);
            int i = packetByteBuf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);

            for (int j = 0; j < defaultedList.size(); ++j) {
                defaultedList.set(j, Ingredient.fromPacket(packetByteBuf));
            }

            ItemStack itemStack = this.validateRecipeOutput(packetByteBuf.readItemStack(), configName);
            return new ConditionedShaplessRecipe(identifier, groupName, itemStack, defaultedList, configName);
        }

        public void write(PacketByteBuf packetByteBuf, ConditionedShaplessRecipe shapelessRecipe) {
            packetByteBuf.writeString(shapelessRecipe.group);
            packetByteBuf.writeString(shapelessRecipe.configName);
            packetByteBuf.writeVarInt(shapelessRecipe.input.size());
            Iterator var3 = shapelessRecipe.input.iterator();

            while (var3.hasNext()) {
                Ingredient ingredient = (Ingredient) var3.next();
                ingredient.write(packetByteBuf);
            }

            packetByteBuf.writeItemStack(shapelessRecipe.output);
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
