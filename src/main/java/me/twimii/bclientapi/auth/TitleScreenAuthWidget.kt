package me.twimii.bclientapi.auth

import com.mojang.blaze3d.systems.RenderSystem
import me.twimii.bclientapi.title.SexierButtonWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.TimeHelper
import net.minecraft.util.Util


val BTN_TEXTURE = Identifier("bclientapi", "textures/gui/authbtn.png") // TODO actual texture
val ICON_TEXTURE = Identifier("bclientapi", "textures/gui/authbtn_icons.png") // icons are 15px no gap

class TitleScreenAuthWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    pressAction: PressAction)
    : SexierButtonWidget(
    x, y, width, height, Text.literal("Auth"), pressAction
    ) {


    var lastTime : Long = 0;

    enum class AuthButtonStatus {
        AUTH_REQUIRED,
        LOGGED_IN,
        LOGGING_IN
    }
    companion object {
        @JvmStatic var status: AuthButtonStatus = AuthButtonStatus.AUTH_REQUIRED
    }

    override fun onPress() {
        if ((Util.getMeasuringTimeMs() - lastTime) < 1000) {
            // Double-clicked
            MinecraftClient.getInstance().toastManager.clear()
            BClientAuthInit.BClientMSLogin()
        } else
            super.onPress()

        lastTime = Util.getMeasuringTimeMs()
    }

    private fun getUV(): Pair<Float,Float> {
        return when (status) {
            AuthButtonStatus.AUTH_REQUIRED -> {
                Pair(0f, 0f)
            }
            AuthButtonStatus.LOGGED_IN -> {
                Pair(15f, 0f)
            }
            AuthButtonStatus.LOGGING_IN -> {
                Pair(30f, 0f)
            }
        }
    }

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderButton(matrices, mouseX, mouseY, delta)

        RenderSystem.setShaderTexture(0, ICON_TEXTURE)
        val (u, v) = getUV()
        drawTexture(
            matrices,
            (x+width)-(height/4), y-(height/4), height/2, height/2, u, v, 15, 15, 100, 15
        )
    }
}