package me.twimii.bclientapi

import com.google.common.collect.ImmutableList
import com.mojang.authlib.GameProfile
import me.twimii.bclientapi.auth.BClientAuthInit.getStatus
import me.twimii.bclientapi.auth.AuthToast
import me.twimii.bclientapi.auth.PlayerHeadSkinGetter
import me.twimii.bclientapi.auth.SessionUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.toast.SystemToast
import net.minecraft.client.toast.Toast
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Color

object GUIUtil {
    val backgroundColor = Color(76, 74, 72, 230)
    const val guiItemWidth = 120
    fun getHeightByWidth(width: Int): Int {
        val screen = MinecraftClient.getInstance().currentScreen!!
        val scalar = screen.height.toFloat() / screen.width.toFloat()
        return (width * scalar).toInt()
    }
    fun drawCenteredText(
        matrices: MatrixStack?,
        textRenderer: TextRenderer,
        text: Text,
        centerX: Int,
        y: Int,
        color: Int
    ) {

        textRenderer.draw(
            matrices,
            text,
            (centerX - textRenderer.getWidth(text) / 2).toFloat(),
            (y - (textRenderer.fontHeight/2)).toFloat(),
            color
        )
    }

    @Deprecated(
        "Use Text object",
        ReplaceWith(
            "loginToast(Text.literal(text))",
            "me.twimii.bclientapi.GUIUtil.loginToast",
            "net.minecraft.text.Text"
        ),
    )
    fun loginToast(text: String) {
        loginToast(Text.literal(text))
    }

    fun loginToast(text: Text) {
        SystemToast.add(
            MinecraftClient.getInstance().toastManager,
            SystemToast.Type.PERIODIC_NOTIFICATION,
            Text.literal("Logging on..."),
            text
        )
    }



    fun multilineLoginToastObj(text: MutableList<out OrderedText>, skinTexture: PlayerHeadSkinGetter): AuthToast {
        return AuthToast.create(
            AuthToast.Type.AUTH_STATUS_TOAST,
            Text.literal("Logged in"),
            text,
            skinTexture
        )
    }

    fun loggedInMessage() {
        MinecraftClient.getInstance().toastManager.add(loggedInMessageObj())
    }

    fun loggedInMessageObj(): AuthToast {
        return multilineLoginToastObj(
            ImmutableList.of(
                Text.literal("Logged in as").asOrderedText(),
                Text.literal("   " + SessionUtils.getSession().username).asOrderedText(),
                Text.literal("BClient auth status: " + getStatus()).asOrderedText(),
                Text.literal("Double click to change account").asOrderedText()
            ), PlayerHeadSkinGetter(SessionUtils.getSession().profile)
        )
    }

    val skins: MutableMap<GameProfile, Identifier?> = mutableMapOf()


}