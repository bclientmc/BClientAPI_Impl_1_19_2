package me.twimii.bclientapi.mixin;

import me.twimii.bclientapi.hacks.DummyOtherClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class SillyRenderHax {

    @Inject(at = @At("HEAD"), method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true)
    private void isLabels(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof DummyOtherClientPlayerEntity) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

}
