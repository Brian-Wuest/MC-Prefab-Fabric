package com.wuest.prefab.mixins;

import com.mojang.authlib.GameProfile;
import com.wuest.prefab.config.EntityPlayerConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Player.class)
public class SavePlayerDataMixin {
	@Shadow @Final private GameProfile gameProfile;

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
		UUID prefabPlayerTag = this.gameProfile.getId();
		EntityPlayerConfiguration prefabConfiguration;

		if (!EntityPlayerConfiguration.playerTagData.containsKey(prefabPlayerTag)) {
			prefabConfiguration = new EntityPlayerConfiguration();

		} else {
			prefabConfiguration = EntityPlayerConfiguration.playerTagData.get(prefabPlayerTag);
		}

		tag.put("PrefabTag", prefabConfiguration.createPlayerTag());
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
		UUID prefabPlayerTag = this.gameProfile.getId();

		EntityPlayerConfiguration prefabConfiguration = new EntityPlayerConfiguration();

		if (tag.contains("PrefabTag")) {
			prefabConfiguration.loadFromNBTTagCompound(tag.getCompound("PrefabTag"));
		}

		if (!EntityPlayerConfiguration.playerTagData.containsKey(prefabPlayerTag)) {
			EntityPlayerConfiguration.playerTagData.put(prefabPlayerTag, prefabConfiguration);
		} else {
			EntityPlayerConfiguration.playerTagData.replace(prefabPlayerTag, prefabConfiguration);
		}
	}
}
