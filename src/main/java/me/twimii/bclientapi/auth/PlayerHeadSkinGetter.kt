package me.twimii.bclientapi.auth

import com.mojang.authlib.GameProfile
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.util.Identifier

class PlayerHeadSkinGetter(private val profile: GameProfile) {
    private var done = false
    private var skin: Identifier? = null

    init {
        MinecraftClient.getInstance().skinProvider.loadSkin(
            profile,
            { type: MinecraftProfileTexture.Type, id: Identifier?, texture: MinecraftProfileTexture ->
                if (type == MinecraftProfileTexture.Type.SKIN) {
                    done = true
                    skin = id
                }
            }, true
        )
    }

    fun getDone(): Boolean {
        return done
    }

    fun getDefault(): Identifier {
        return DefaultSkinHelper.getTexture(profile.id)
    }

    fun getSkin(): Identifier? {
        return skin
    }

}