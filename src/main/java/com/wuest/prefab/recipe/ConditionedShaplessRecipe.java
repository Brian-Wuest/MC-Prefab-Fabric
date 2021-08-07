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
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

import java.util.Iterator;

public class ConditionedShaplessRecipe extends ShapelessRecipe {
	private final ResourceLocation id;
	private final String group;
	private final ItemStack output;
	private final NonNullList<Ingredient> input;
	private final String configName;

	public ConditionedShaplessRecipe(ResourceLocation id, String group, ItemStack output, NonNullList<Ingredient> input, String configName) {
	    super(id, group, output, input);

		this.id = id;
		this.group = group;
		this.output = output;
		this.input = input;
		this.configName = configName;
	}

	public ResourceLocation getId() {
		return this.id;
	}

	public RecipeSerializer<?> getSerializer() {
		return ModRegistry.ConditionedShapelessRecipeSeriaizer;
	}

	@Environment(EnvType.CLIENT)
	public String getGroup() {
		return this.group;
	}

	public ItemStack getResultItem() {
		return this.output;
	}

	public NonNullList<Ingredient> getIngredients() {
		return this.input;
	}

	public boolean matches(CraftingContainer craftingInventory, Level world) {
		StackedContents recipeFinder = new StackedContents();
		int i = 0;

		for (int j = 0; j < craftingInventory.getContainerSize(); ++j) {
			ItemStack itemStack = craftingInventory.getItem(j);
			if (!itemStack.isEmpty()) {
				++i;
				recipeFinder.accountStack(itemStack, 1);
			}
		}

		return i == this.input.size() && recipeFinder.canCraft(this, (IntList) null);
	}

	public ItemStack craft(CraftingContainer craftingInventory) {
		return this.output.copy();
	}

	@Environment(EnvType.CLIENT)
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= this.input.size();
	}

	public static class Serializer implements RecipeSerializer<ConditionedShaplessRecipe> {
		public ConditionedShaplessRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
			String groupName = GsonHelper.getAsString(jsonObject, "group", "");
			String configName = GsonHelper.getAsString(jsonObject, "configName", "");
			NonNullList<Ingredient> defaultedList = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));

			if (defaultedList.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else if (defaultedList.size() > 9) {
				throw new JsonParseException("Too many ingredients for shapeless recipe");
			} else {
				ItemStack itemStack = this.validateRecipeOutput(ConditionedShapedRecipe.getItemStack(GsonHelper.getAsJsonObject(jsonObject, "result")), configName);
				return new ConditionedShaplessRecipe(identifier, groupName, itemStack, defaultedList, configName);
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

		public ConditionedShaplessRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
			String groupName = packetByteBuf.readUtf(32767);
			String configName = packetByteBuf.readUtf(32767);
			int i = packetByteBuf.readVarInt();
			NonNullList<Ingredient> defaultedList = NonNullList.withSize(i, Ingredient.EMPTY);

			for (int j = 0; j < defaultedList.size(); ++j) {
				defaultedList.set(j, Ingredient.fromNetwork(packetByteBuf));
			}

			ItemStack itemStack = this.validateRecipeOutput(packetByteBuf.readItem(), configName);
			return new ConditionedShaplessRecipe(identifier, groupName, itemStack, defaultedList, configName);
		}

		public void toNetwork(FriendlyByteBuf packetByteBuf, ConditionedShaplessRecipe shapelessRecipe) {
			packetByteBuf.writeUtf(shapelessRecipe.group);
			packetByteBuf.writeUtf(shapelessRecipe.configName);
			packetByteBuf.writeVarInt(shapelessRecipe.input.size());
			Iterator var3 = shapelessRecipe.input.iterator();

			while (var3.hasNext()) {
				Ingredient ingredient = (Ingredient) var3.next();
				ingredient.toNetwork(packetByteBuf);
			}

			packetByteBuf.writeItem(shapelessRecipe.output);
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
