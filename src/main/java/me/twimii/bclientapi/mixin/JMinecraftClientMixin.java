package me.twimii.bclientapi.mixin;

import me.twimii.bclientapi.BClientAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicType;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class JMinecraftClientMixin {
    @Inject(method = "getMusicType", at = @At("TAIL"), cancellable = true)
    private void getMusicType(CallbackInfoReturnable<MusicSound> cir) {
    }
}
