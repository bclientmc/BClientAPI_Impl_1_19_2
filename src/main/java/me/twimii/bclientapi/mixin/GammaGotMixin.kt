package me.twimii.bclientapi.mixin

import me.twimii.bclientapi.module.GammaController
import net.minecraft.client.option.SimpleOption
import net.minecraft.client.render.LightmapTextureManager
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect


@Mixin(LightmapTextureManager::class)
abstract class GammaGotMixin {
    @Redirect(at = At(value="INVOKE", target="Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"),
        method = ["update(F)V"])
    private fun getGamma(s: SimpleOption<Double>) : Any {
        return if (GammaController.customGamma) {
            GammaController.fullbrightGamma
        } else {
            s.value
        }
    }
}

