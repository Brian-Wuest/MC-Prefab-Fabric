package com.wuest.prefab.mixins;

import com.wuest.prefab.ClientModRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientInitMixin {
    // signal that we want to inject into a method
    @Inject(
            method = "<init>",  // the jvm bytecode signature for the constructor
            at = @At("TAIL")  // signal that this void should be run at the method TAIL, meaning the last opcode
    )
    public void constructorHead(
            // you will need to put any parameters the constructor accepts here, they will be the actual passed values
            // it also needs to accept a special argument that mixin passes to this injection method
            RunArgs args,
            CallbackInfo ci
    ) {
        ClientModRegistry.RegisterBlockRenderer();
    }
}
