package me.twimii.bclientapi

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
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
}