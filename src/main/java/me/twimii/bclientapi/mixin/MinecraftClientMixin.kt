package me.twimii.bclientapi.mixin

import net.minecraft.client.MinecraftClient
import net.minecraft.client.RunArgs
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(MinecraftClient::class)
abstract class MinecraftClientMixin() {

    @Redirect(method = ["<init>"], at = At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;isDemo:Z", opcode = Opcodes.PUTFIELD))
    private fun injected(something: MinecraftClient, x: Boolean) {
    }


}