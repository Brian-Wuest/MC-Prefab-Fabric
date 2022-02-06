package com.wuest.prefab.mixins;

import com.wuest.prefab.config.EntityPlayerConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Player.class)
public class SavePlayerDataMixin {
	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
		UUID playerTag = tag.getUUID("UUID");
		EntityPlayerConfiguration configuration;

		if (!EntityPlayerConfiguration.playerTagData.containsKey(playerTag)) {
			configuration = new EntityPlayerConfiguration();

		} else {
			configuration = EntityPlayerConfiguration.playerTagData.get(playerTag);
		}

		tag.put("PrefabTag", configuration.createPlayerTag());
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
		UUID playerTag = tag.getUUID("UUID");

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
