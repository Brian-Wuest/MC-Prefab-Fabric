package com.wuest.prefab.mixins;

import com.wuest.prefab.config.EntityPlayerConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public class SavePlayerDataMixin {
	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void writeCustomDataToTag(NbtCompound tag, CallbackInfo ci) {
		UUID playerTag = tag.getUuid("UUID");
		EntityPlayerConfiguration configuration;

		if (!EntityPlayerConfiguration.playerTagData.containsKey(playerTag)) {
			configuration = new EntityPlayerConfiguration();

		} else {
			configuration = EntityPlayerConfiguration.playerTagData.get(playerTag);
		}

		tag.put("PrefabTag", configuration.createPlayerTag());
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void readCustomDataFromTag(NbtCompound tag, CallbackInfo ci) {
		UUID playerTag = tag.getUuid("UUID");

		EntityPlayerConfiguration configuration = new EntityPlayerConfiguration();

		if (tag.contains("PrefabTag")) {
			configuration.loadFromNBTTagCompound(tag.getCompound("PrefabTag"));
		}

		if (!EntityPlayerConfiguration.playerTagData.containsKey(playerTag)) {
			EntityPlayerConfiguration.playerTagData.put(playerTag, configuration);
		} else {
			EntityPlayerConfiguration.playerTagData.replace(playerTag, configuration);
		}
	}
}
