package me.twimii.bclientapi.hacks

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.client.world.ClientWorld

class DummyOtherClientPlayerEntity(world: ClientWorld, profile: GameProfile) : OtherClientPlayerEntity(world, profile, null) {
    override fun isSpectator(): Boolean {
        return false
    }

    override fun isCreative(): Boolean {
        return false
    }


}